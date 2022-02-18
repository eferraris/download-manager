# download-manager

Boost AWS S3 file download

## What is DownloadManager?

It is designed to optimize file downloading from AWS S3 by spliting the file in several parts and downloads each part separately. It gives you the possibility to paralelize this process giving you a performance boost. It will help you when are trying to download large files with poor internet connection: as each part is downloaded and persisted, when the process fails, it will start from where it was left.

## Usage

    val manager = MultipartDownloadManagerBuilder
        .standard(s3Client, bucketName, keyName, filePath)
        .withPartThreshold( 1024 * 1024 * 20L ) /* size (bytes) of each part */
        .withParallelResolution( true )
        .withLogReport( true )
        .build()
    
    manager.download()

## Gradle

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/eferraris/download-manager")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    
    dependencies {
        implementation("com.eferraris:download-manager:1.1.3")
    }

