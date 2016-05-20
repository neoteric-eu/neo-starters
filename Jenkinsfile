node {
    stage 'Git Checkout'

    // Checkout code from repository and update any submodules
    checkout  scm

    stage 'Code compile'

    env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
    sh 'mvn clean package'
}
