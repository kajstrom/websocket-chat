(ns websocket-chat.validation
  (:require [struct.core :as st]))

(def participant-scheme
  {
    :name [[st/required :message "Name is required"]
           [st/max-count 15 :message "Name cannot be longer than 15 chars"]]
    })