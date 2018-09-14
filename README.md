# awsrekognitionlambda

./gradlew clean eclipse build

HelloLambda.java::handleRequest(S3Event e3event, Context context)
    e3event parameters has s3 bucket name, region and image location details which will be used
    to read the image byte inputstream, submit it to AWS Rekognition API to read labels and texts of the image.
    The imagePath(key) has the house_pic id as well.
    The image text along with house_pic id are  submitted to maaghar webhook to save and index image text in CloudSearch.
    
