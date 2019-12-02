+++
title = "Summary"
weight = 16
pre = ""
+++


| Measurement          | MIN in ms | P99 in ms | FASTEST COLD-START in ms |
|----------------------|-----------|-----------|--------------------------|
| Python in Lambda     | 46        | 156       | 581                      |
| Springboot in Lambda | 41        | 11775     | 11500                    |
| Dagger2 in Lambda    | 36        | 4413      | 4700                     |
| no DI in Lambda      | 38        | 4492      | 4700                     |