package com.example.demo;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.camera.core.Camera;
        import androidx.camera.core.CameraSelector;
        import androidx.camera.core.ExperimentalGetImage;
        import androidx.camera.core.ImageAnalysis;
        import androidx.camera.core.ImageProxy;
        import androidx.camera.core.Preview;
        import androidx.camera.view.PreviewView;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;

        import android.content.Context;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Matrix;
        import android.graphics.Paint;
        import android.os.Bundle;
        import android.widget.TextView;

        import java.nio.ByteBuffer;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Locale;
        import java.util.concurrent.ExecutionException;
        import androidx.camera.lifecycle.ProcessCameraProvider;
        import androidx.lifecycle.LifecycleOwner;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.common.util.concurrent.ListenableFuture;
        import com.google.mlkit.vision.common.InputImage;
        import com.google.mlkit.vision.pose.Pose;
        import com.google.mlkit.vision.pose.PoseDetection;
        import com.google.mlkit.vision.pose.PoseDetector;
        import com.google.mlkit.vision.pose.PoseLandmark;
        import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

public class MainActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    int PERMISSION_REQUESTS = 1;

    PreviewView previewView;

    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    PoseDetectorOptions options =
            new PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .build();

    PoseDetector poseDetector = PoseDetection.getClient(options);

    Canvas canvas;

    Paint mPaint = new Paint();

    Display display;

    Bitmap bitmap4Save;

    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmap4DisplayArrayList = new ArrayList<>();

    ArrayList<Pose> poseArrayList = new ArrayList<>();
    String currentPoseStatus = "down";
    int poseCounterValue = 0;
    TextView poseAngle;
    TextView poseCounterNew;
    TextView poseStatus;
    TextView angleSpeed;

    long downToUpTransitionTimestamp = 0;
    long upToDownTransitionTimestamp = 0;
    boolean isRunning = false;

    @ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView = findViewById(R.id.previewView);

        display = findViewById(R.id.displayOverlay);

        poseAngle = findViewById(R.id.angle);
        poseCounterNew = findViewById(R.id.counter);
        poseStatus = findViewById(R.id.poseStatus);
        angleSpeed = findViewById(R.id.angleSpeed);

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(5);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    Runnable RunMlkit = new Runnable() {
        @Override
        public void run() {
            poseDetector.process(InputImage.fromBitmap(bitmapArrayList.get(0), 0))
                    .addOnSuccessListener(new OnSuccessListener<Pose>() {
                        @Override
                        public void onSuccess(Pose pose) {
                            if (pose != null) {
                                poseArrayList.add(pose);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                        }
                    });
        }
    };

    static double getAngle(PoseLandmark shoulder, PoseLandmark elbow, PoseLandmark wrist) {
        double angle = Math.toDegrees(
                Math.atan2(wrist.getPosition().y - elbow.getPosition().y, wrist.getPosition().x - elbow.getPosition().x)
                        - Math.atan2(shoulder.getPosition().y - elbow.getPosition().y, shoulder.getPosition().x - elbow.getPosition().x)
        );
        angle = Math.abs(angle);
        if (angle > 180) {
            angle = 360.0 - angle;
        }
        return angle;
    }

    @ExperimentalGetImage
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ActivityCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                ByteBuffer byteBuffer = imageProxy.getImage().getPlanes()[0].getBuffer();
                byteBuffer.rewind();
                Bitmap bitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(byteBuffer);

                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                matrix.postScale(-1, 1);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, imageProxy.getWidth(), imageProxy.getHeight(), matrix, false);

                bitmapArrayList.add(rotatedBitmap);

                if (poseArrayList.size() >= 1) {
                    canvas = new Canvas(bitmapArrayList.get(0));
                    Pose pose = poseArrayList.get(0);
                    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
                    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
                    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);

                    if (rightShoulder != null && rightElbow != null && rightWrist != null) {
                        double angle = getAngle(rightShoulder, rightElbow, rightWrist);
                        updateangle(angle);

                        int numConnections = getNumConnections();
                        for (int i = 0; i < numConnections; i++) {
                            int startIdx = getConnectionStartIndex(i);
                            int endIdx = getConnectionEndIndex(i);
                            PoseLandmark startLandmark = poseArrayList.get(0).getPoseLandmark(startIdx);
                            PoseLandmark endLandmark = poseArrayList.get(0).getPoseLandmark(endIdx);
                            if (startLandmark != null && endLandmark != null) {
                                canvas.drawLine(
                                        startLandmark.getPosition().x,
                                        startLandmark.getPosition().y,
                                        endLandmark.getPosition().x,
                                        endLandmark.getPosition().y,
                                        mPaint
                                );
                            }
                        }
                    }

                    bitmap4DisplayArrayList.clear();
                    bitmap4DisplayArrayList.add(bitmapArrayList.get(0));
                    bitmap4Save = bitmapArrayList.get(bitmapArrayList.size() - 1);
                    bitmapArrayList.clear();
                    bitmapArrayList.add(bitmap4Save);
                    poseArrayList.clear();
                    isRunning = false;
                }

                if (bitmapArrayList.size() >= 1 && !isRunning) {
                    RunMlkit.run();
                    isRunning = true;
                }

                if (bitmap4DisplayArrayList.size() >= 1) {
                    display.getBitmap(bitmap4DisplayArrayList.get(0));
                }

                imageProxy.close();
            }
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
    }

    private double calculateAngularSpeed(double currentAngle, double previousAngle, long previousTimestamp, long currentTimestamp) {
        double angleChange = Math.abs(currentAngle - previousAngle);
        double timeDiffInSeconds = (currentTimestamp - previousTimestamp) / 1000.0;
        return angleChange / timeDiffInSeconds;
    }

    private void updateAngularSpeed(double angularSpeed) {
        String speedText = String.format(Locale.getDefault(), "Speed: %.2f /s", angularSpeed);
        angleSpeed.setText(speedText);
    }

    private void updateangle(double angle) {
        String angleText = String.format(Locale.getDefault(), "Angle: %.2f", angle);
        poseAngle.setText(angleText);

        if (angle > 145 && !currentPoseStatus.equals("down")) {
            currentPoseStatus = "down";
            downToUpTransitionTimestamp = System.currentTimeMillis();
            incrementPoseCounter();
        } else if (angle < 55 && !currentPoseStatus.equals("up")) {
            currentPoseStatus = "up";
            upToDownTransitionTimestamp = System.currentTimeMillis();
        }

        if (downToUpTransitionTimestamp != 0 && upToDownTransitionTimestamp != 0) {
            long downToUpTransitionTime = upToDownTransitionTimestamp - downToUpTransitionTimestamp;
            long upToDownTransitionTime = System.currentTimeMillis() - upToDownTransitionTimestamp;

            double downToUpSpeed = calculateSpeed(downToUpTransitionTime);
            double upToDownSpeed = calculateSpeed(upToDownTransitionTime);

            String speedText = String.format(Locale.getDefault(), "Repetitions Speed: %.2f /s", downToUpSpeed, upToDownSpeed);
            angleSpeed.setText(speedText);

            downToUpTransitionTimestamp = 0;
            upToDownTransitionTimestamp = 0;
        }

        poseCounterNew.setText(String.valueOf(poseCounterValue));
        poseStatus.setText(currentPoseStatus);
    }

    private double calculateSpeed(long transitionTime) {
        return Math.abs(90.0 / transitionTime * 1000.0);
    }

    private void incrementPoseCounter() {
        poseCounterValue++;
    }

    public int getNumConnections() {
        return 26;
    }

    public int getConnectionStartIndex(int connectionIndex) {
        switch (connectionIndex) {
            case 0: return PoseLandmark.LEFT_SHOULDER;
            case 1: return PoseLandmark.RIGHT_SHOULDER;
            case 2: return PoseLandmark.RIGHT_ELBOW;
            case 3: return PoseLandmark.RIGHT_WRIST;
            case 4: return PoseLandmark.RIGHT_PINKY;
            case 5: return PoseLandmark.RIGHT_INDEX;
            case 6: return PoseLandmark.RIGHT_WRIST;
            case 7: return PoseLandmark.RIGHT_SHOULDER;
            case 8: return PoseLandmark.RIGHT_HIP;
            case 9: return PoseLandmark.RIGHT_KNEE;
            case 10: return PoseLandmark.RIGHT_ANKLE;
            case 11: return PoseLandmark.RIGHT_FOOT_INDEX;
            case 12: return PoseLandmark.RIGHT_HEEL;
//            left side
            case 13: return PoseLandmark.LEFT_SHOULDER;
            case 14: return PoseLandmark.LEFT_ELBOW;
            case 15: return PoseLandmark.LEFT_WRIST;
            case 16: return PoseLandmark.LEFT_PINKY;
            case 17: return PoseLandmark.LEFT_INDEX;
            case 18: return PoseLandmark.LEFT_WRIST;
            case 19: return PoseLandmark.LEFT_SHOULDER;
            case 20: return PoseLandmark.LEFT_HIP;
            case 21: return PoseLandmark.LEFT_KNEE;
            case 22: return PoseLandmark.LEFT_ANKLE;
            case 23: return PoseLandmark.LEFT_FOOT_INDEX;
            case 24: return PoseLandmark.LEFT_HEEL;


            case 25: return PoseLandmark.RIGHT_HIP;
            default: return -1; // Handle invalid connection indices
        }
    }

    public int getConnectionEndIndex(int connectionIndex) {
        switch (connectionIndex) {
            case 0: return PoseLandmark.RIGHT_SHOULDER;
            case 1: return PoseLandmark.RIGHT_ELBOW;
            case 2: return PoseLandmark.RIGHT_WRIST;
            case 3: return PoseLandmark.RIGHT_PINKY;
            case 4: return PoseLandmark.RIGHT_INDEX;
            case 5: return PoseLandmark.RIGHT_WRIST;
            case 6: return PoseLandmark.RIGHT_THUMB;
            case 7: return PoseLandmark.RIGHT_HIP;
            case 8: return PoseLandmark.RIGHT_KNEE;
            case 9: return PoseLandmark.RIGHT_ANKLE;
            case 10: return PoseLandmark.RIGHT_FOOT_INDEX;
            case 11: return PoseLandmark.RIGHT_HEEL;
            case 12: return PoseLandmark.RIGHT_ANKLE;
//            left side
            case 13: return PoseLandmark.LEFT_ELBOW;
            case 14: return PoseLandmark.LEFT_WRIST;
            case 15: return PoseLandmark.LEFT_PINKY;
            case 16: return PoseLandmark.LEFT_INDEX;
            case 17: return PoseLandmark.LEFT_WRIST;
            case 18: return PoseLandmark.LEFT_THUMB;
            case 19: return PoseLandmark.LEFT_HIP;
            case 20: return PoseLandmark.LEFT_KNEE;
            case 21: return PoseLandmark.LEFT_ANKLE;
            case 22: return PoseLandmark.LEFT_FOOT_INDEX;
            case 23: return PoseLandmark.LEFT_HEEL;
            case 24: return PoseLandmark.LEFT_ANKLE;

            case 25: return PoseLandmark.LEFT_HIP;
            default: return -1; // Handle invalid connection indices
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }
}
