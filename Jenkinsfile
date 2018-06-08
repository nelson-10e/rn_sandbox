pipeline {
  agent any
  stages {
    stage('init') {
      agent {
        node {
          label 'shared'
        }

      }
      steps {
        script{
            docker.build("runmymind/docker-android-sdk")
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
            def image = docker.image("runmymind/docker-android-sdk")
                                        image.pull()
                                        image.inside {
                                            sh '''
                                            ls
                                            '''
                                        }
        }
      }
    }
  }
}