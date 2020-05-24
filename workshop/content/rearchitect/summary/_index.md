+++
title = "Summary"
weight = 33
pre = ""
+++


| Measurement          | MIN in ms | AVG in ms | P99 in ms | FASTEST COLD-START in ms | AVG execution cost per milliom invocations |
|----------------------|-----------|-----------|-----------|--------------------------|---------------------------------------------
| Lambda on Python     | 46        |           | 156       | 581                      |                                            |
| Lambda on Springboot | 41        |           | 11775     | 11500                    |                                            |
|                      |           |           |           |                          |                                            |
| Lambda on Quarkus (512 MB)  | 52        | 81        | 154       | 726                      | $ 1.03                                     |
| Lambda on Quarkus (3008 MB) | 46        | 67        | 125       | 638                      | $ 5.10                                     |
| Lambda on Micronaut         |           |           |           |                          |                                            |
| Lambda on GraalVM (512 MB)  | 49        | 80        | 171       | 691                      | $ 1.03                                     |
| Lambda on GraalVM (3008 MB) | 48        | 68        | 123       | 578                      | $ 5.10                                     |