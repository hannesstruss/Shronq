package de.hannesstruss.shronq.data.s3sync

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import dagger.Module
import dagger.Provides

@Module
class S3SyncModule {
  @Provides fun s3Client(creds: S3CredentialsStore): AmazonS3Client {
    val credentials = BasicAWSCredentials(creds.accessKey, creds.secretKey)
    return AmazonS3Client(credentials, Region.getRegion(Regions.EU_CENTRAL_1))
  }
}
