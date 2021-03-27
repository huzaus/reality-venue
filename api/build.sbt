import Dependencies._

libraryDependencies ++= commonDependencies ++
                        testDependencies ++
                        tapirDependencies ++
                        circleDependencies :+
                        http4sCircle