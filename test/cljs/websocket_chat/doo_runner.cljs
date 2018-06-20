(ns websocket-chat.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [websocket-chat.core-test]))

(doo-tests 'websocket-chat.core-test)

