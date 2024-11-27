pipeline {
    agent any
    environment {
        // Inject Jenkins credentials into environment variables
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID') // Jenkins credential ID for Access Key
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY') // Jenkins credential ID for Secret Key
        AWS_SESSION_TOKEN = credentials('AWS_SESSION_TOKEN') // Jenkins credential ID for Session Token
    }
    stages {
        stage('volume_validation') {
            steps {
                script {
                   

                    // Run shell commands
                    sh """
                    echo "Listing current directory..."
                    echo "AWS_ACCESS_KEY_ID is: \$AWS_ACCESS_KEY_ID"
                    ls -l

                    # Install boto3
                    python3 -m pip install boto3 --break-system-packages
                    python3 -c 'import boto3; print("boto3 is installed")'

                    # Run volume_check.py script
                    python volume_check.py
                    """
                }
            }
        }
    }
}
