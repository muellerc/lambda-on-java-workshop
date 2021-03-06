import boto3
from botocore.config import Config
import json
import os
import uuid

TABLE_NAME = os.environ['TABLE_NAME']
BUCKET_NAME = os.environ['BUCKET_NAME']
AWS_REGION = os.environ['AWS_REGION']

dynamodb = boto3.client('dynamodb', AWS_REGION)
s3 = boto3.client('s3', AWS_REGION)

def lambda_handler(event, context):
    pet = json.loads(event['body'])

    pet['id'] = str(uuid.uuid4())

    dynamodb.put_item(
        TableName=TABLE_NAME,
        Item={
            'id': {'S': pet['id']},
            'name': {'S': pet['name']},
            'type': {'S': pet['type']},
            'birthday': {'S': pet['birthday']}
        }
    )

    s3.put_object (
        Bucket=BUCKET_NAME,
        Key='medical-record/' + pet['id'],
        Body=json.dumps(pet)
    )

    return {
        'statusCode': 200,
        'body': json.dumps(pet)
    }
