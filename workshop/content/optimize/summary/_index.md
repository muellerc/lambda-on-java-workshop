+++
title = "Summary"
weight = 32
pre = ""
+++

| Measurement                  | MIN in ms | P99 in ms | FASTEST COLD-START in ms |
|------------------------------|-----------|-----------|--------------------------|
| Python in Lambda             | 46        |   156     |   581                    |
| Springboot in Lambda         | 41        | 11775     | 11500                    |
| **AWS Java SDK v2**          | **40**    | **4884**  | **5000**                 |
|                              |           |           |                          |
| Reduce Dependencies          | 37        | 4303      | 4400                     |
| Handler Interface            | 41        | 2880      | 4200                     |
| Packaging Mechanism          | 40        | 4942      | 5000                     |
| Multithreading               | 32        |  120      | 3600                     |
| Java 11                      | 42        | 5019      | 4900                     |
| JVM Options                  | 41        | 5051      | 4900                     |
| Prime Resources              |           |           |                          |
| Lambda Layers                |           |           |                          |
| ALB                          |           |           |                          |
| Coretto Crypto Provider      |           |           |                          |
|                              |           |           |                          |
| **Optimized Implementation** | **33**    | **121**   | **2400**                 |