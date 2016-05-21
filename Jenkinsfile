node {
    stage 'Checkout'
    checkout scm

    stage 'Build'
     mvn 'clean package -DskipTests'

    stage 'Test'
     mvn 'test'

    stage 'Code quality'
    echo 'Mocked Sonar for now'
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}