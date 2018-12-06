package io.freestar.ssp.server.common.core.system.job;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.File;

public class GoogleCloudHandler {
    private static final Logger log = LoggerFactory.getLogger(GoogleCloudHandler.class);
    private static GoogleCloudHandler singleton;
    private final Storage storage;
    private final String bucketName;

    private GoogleCloudHandler() {
// Your Google Cloud Platform project ID.
//        String projectId = "freestar-157323";
        // Instantiates a client
        storage = StorageOptions.getDefaultInstance().getService();
        // The name for the new bucket
        bucketName = "ssp-storage";
    }


    public void uploadFile(String fileName, String blobName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, blobName);
        RandomAccessFile f = new RandomAccessFile(fileName, "r");
        byte[] content = new byte[(int) f.length()];
        f.readFully(content);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        try (WriteChannel writer = storage.writer(blobInfo)) {
            try {
                writer.write(ByteBuffer.wrap(content, 0, content.length));
                log.info("File written to GCS ("+blobName+")");
            } catch (Exception ex) {
                log.error("Error writing file to GCS ("+blobName+")");
                throw ex;
            }
        }

    }

    public void uploadFile(String fileName, String blobName, String contentType) throws IOException {
        log.info("upload "+contentType+" to GCS ("+blobName+")");
        BlobId blobId = BlobId.of(bucketName, blobName);
        RandomAccessFile f = new RandomAccessFile(fileName, "r");
        byte[] content = new byte[(int) f.length()];
        f.readFully(content);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        writeContent(blobInfo, contentType, content);
    }

    private void writeContent(BlobInfo blobInfo, String contentType, byte[] content) throws IOException {
        try (WriteChannel writer = storage.writer(blobInfo)) {
            try {
                writer.write(ByteBuffer.wrap(content, 0, content.length));
                log.debug(contentType+" content written to GCS");
            } catch (Exception ex) {
                log.error("Error writing "+contentType+" content to GCS");
                throw ex;
            }
        }
    }

    public void downloadFile(File file, String blobName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);

        ByteBuffer buffer = ByteBuffer.wrap(blob.getContent());

        FileOutputStream fos = new FileOutputStream(file);
        FileChannel fc = fos.getChannel();
        fc.write(buffer);
        fc.close();
        fos.close();
    }


//    public boolean exists(String blobName) {
//        BlobId blobId = BlobId.of(bucketName, blobName);
//        return storage.get(blobId) != null;
//    }

    public void removeFile(String blobName) {
        BlobId blobId = BlobId.of(bucketName, blobName);
        storage.delete(blobId);
    }

    public synchronized static GoogleCloudHandler getInstance() {
        if (singleton == null) {
            singleton = new GoogleCloudHandler();
        }
        return singleton;
    }

    public void populateFile(String blobName, String contentType, byte[] content) throws IOException {
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        writeContent(blobInfo, contentType, content);
    }

    public String extractStringFromFile(String blobName) {
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        return new String(blob.getContent(Blob.BlobSourceOption.generationMatch()));
    }

    public byte[] downloadFile(String blobName) {
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        return blob.getContent(Blob.BlobSourceOption.generationMatch());
    }

}
