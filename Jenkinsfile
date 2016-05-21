node {
    stage 'Checkout'
    checkout scm

    stage 'Build'
     mvn 'clean package -DskipTests'

    stage 'Test'
     mvn 'test'

    stage 'Code quality'
    mvn 'sonar:sonar'
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}