## I was getting following issue while running the pipeline 

```
17:49:00 #12 exporting manifest list sha256:bf2bf32b376ee2d72070f063429d1b4af8109019ddb56b6ea9329a0f944d9b90 0.0s done 17:49:00 #12 pushing layers 0.1s done 17:49:00 #12 ERROR: failed to push docker.io/anantgsaraf/web-app:feature-b3116: push access denied, repository does not exist or may require authorization: server message: insufficient_scope: authorization failed 
17:49:00 ------ 17:49:00 > exporting to image: 
17:49:00 ------ 17:49:00 WARNING: current commit information was not captured by the build: failed to read current commit information with git rev-parse --is-inside-work-tree 17:49:00 ERROR: failed to solve: failed to push docker.io/anantgsaraf/web-app:feature-b3116: push access denied, repository does not exist or may require authorization: server message: insufficient_scope: authorization failed
```

* The error was due to following line

```
registry = 'docker.io/anantgsaraf'
```

* I changed it to 

```
registry = 'docker.io'
```

* Now it is working fine

---
