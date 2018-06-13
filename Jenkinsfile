pipeline {
    environment{
        DOCKER_REGISTRY = '320083444745.dkr.ecr.us-west-2.amazonaws.com'
        DOCKER_REPO =  "$DOCKER_REGISTRY/10e/builders"
        DOCKER_IMAGE = "$DOCKER_REPO:react_native_${jobDetails.branchName().replaceAll(/[^a-zA-Z0-9-]/, '_')}"
    }
  agent any
  stages {
    stage('init') {
      agent {
        node {
          label 'shared'
          docker.withRegistry($DOCKER_REGISTRY,'credentials-id'){

          }
        }

      }
      steps {
        script{
        sh "echo init"
        }
      }
    }
    stage('build_deploy') {
      agent {
        node {
          label 'shared'
        }

      }
      steps {
        script{
            def image = docker.image("runmymind/docker-android-sdk:alpine-lazydl")
            image.pull()
            image.inside {
                sh '''
                cd android
                ./gradlew clean build
                '''
            }
        }
      }
    }
  }
}