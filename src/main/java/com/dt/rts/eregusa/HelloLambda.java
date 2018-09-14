package com.dt.rts.eregusa;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;


/**
 * gradlew build
 * gradlew run
 * @author abed.ali
 *
 */
public class HelloLambda implements RequestHandler<S3Event, String>{

	public static void main(String[] args) {
    	System.out.println("main::Abed---Hello World!!!!!!!");
    	String key = "house_pics/pictures/000/000/017/original/Screen_Shot_2018-01-04_at_4.48.33_PM.png";
    	String[] keys = key.split("/");
    	System.out.println("record id=" + keys[keys.length - 3]);
    }

	@Override
	public String handleRequest(S3Event e3event, Context context) {
		// TODO Auto-generated method stub
		System.out.println("maaghar.handleRequest--!!!!!!!S3EventPayload=" + e3event.toJson());
		List<S3EventNotificationRecord> list = e3event.getRecords();
		list.forEach(item -> {
			String region = item.getAwsRegion();
			String eventName = item.getEventName(); //ObjectCreated:Put
			String eventSource = item.getEventSource(); //"aws:s3"
			S3Entity s3entity = item.getS3();
			S3BucketEntity s3BucketEntity = s3entity.getBucket(); 
			String bucketName = s3BucketEntity.getName();//maaghar
			S3ObjectEntity s3ObjectEntity = s3entity.getObject();
			String pathtoFile = s3ObjectEntity.getKey(); //house_pics/pictures/000/000/015/original/Screen_Shot_2018-08-30_at_5.36.16_PM.png
			String urlDecodedKey = s3ObjectEntity.getKey(); //house_pics/pictures/000/000/015/original/Screen_Shot_2018-08-30_at_5.36.16_PM.png
			System.out.println("maaghar.handleRequest::bucketname=" + bucketName);
			if(pathtoFile.indexOf("original") > -1) {
				System.out.println("maaghar.handleRequest::Ok, this is a picture we wanted to process..." + pathtoFile);
				
				
				
				AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                    .withRegion(region)
	                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
	                    //.withCredentials(new ProfileCredentialsProvider()) //This is to build from ~/.aws/credentials localmachine
	                    .build();

	            // Get an object and print its contents.
	            System.out.println("Downloading an object...creating S3Object instance......");
	            S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucketName, pathtoFile));
	            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
	            System.out.println("Content: ");
	            S3ObjectInputStream s3objIS = fullObject.getObjectContent();
	            
	            ByteBuffer imageBytes = null;
	            try (InputStream inputStream = s3objIS) {
	                imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
	            }
	            catch(IOException e) {
	            	System.err.println("Error in processing the image");
	            }
	            
	            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

	            DetectTextRequest request  = new DetectTextRequest().withImage(new Image()
                        .withBytes(imageBytes));
	            		
	            /*
	            DetectLabelsRequest request = new DetectLabelsRequest()
	                    .withImage(new Image()
	                            .withBytes(imageBytes))
	                    .withMaxLabels(10)
	                    .withMinConfidence(77F);
	            
	            try {

	                DetectLabelsResult result = rekognitionClient.detectLabels(request);
	                List <Label> labels = result.getLabels();

	                System.out.println("Detected labels for " + pathtoFile);
	                for (Label label: labels) {
	                   System.out.println(label.getName() + ": " + label.getConfidence().toString());
	                }

	            } catch (AmazonRekognitionException e) {
	                e.printStackTrace();
	            }
	            */
	            String text = "";
	            try {

	            	DetectTextResult result = rekognitionClient.detectText(request);
	                List <TextDetection> texts = result.getTextDetections();

	                System.out.println("Detected labels for " + pathtoFile);
	                for (TextDetection textDetection: texts) {
	                   System.out.println(textDetection.getDetectedText() + ": " + textDetection.getConfidence().toString());
	                   text += textDetection.getDetectedText() + "|";
	                }

	            } catch (AmazonRekognitionException e) {
	                e.printStackTrace();
	            }
	            try {
	            	String[] keys = pathtoFile.split("/");
	            	postJsonUsingHttpClient(keys[keys.length - 3], text);
	            }catch(Exception e) {
	            	System.out.println("maaghar.handleRequest::Exception, " + e.getMessage());
	            }
	            
			} else {
				System.out.println("maaghar.handleRequest::Ok, Ignoring the picture..." + pathtoFile);
			}
		});
		
		return e3event.toString();
	}
	
	private void postJsonUsingHttpClient(String recordId, String content) 
			  throws ClientProtocolException, IOException {
			    CloseableHttpClient client = HttpClients.createDefault();
			    String url = "http://api.maaghar.com/api/1/house_pics/" + recordId + "/lambdaRekognition";
			    System.out.println("OK Rekognition submitting labels to " + url);
			    HttpPost httpPost = new HttpPost(url);
			    
			    String json = "{\"rekognition_text\":\"" + content + "\"}";
			    StringEntity entity = new StringEntity(json);
			    httpPost.setEntity(entity);
			    httpPost.setHeader("Accept", "application/json");
			    httpPost.setHeader("Content-type", "application/json");
			    
			    CloseableHttpResponse response = client.execute(httpPost);
			    if (response.getStatusLine().getStatusCode() == 200)
			    	System.out.println("OK Rekognition labels are submitted successfully.");
			    client.close();
	}
	
}
