(ns lingo.core
  (:use [lingo.features :only [feature]]
        [clojure.core.match :only [match]])
  (:require [lingo.simplenlg-wrapper :as snlg])
  (:import (simplenlg.framework NLGFactory CoordinatedPhraseElement)
           (simplenlg.lexicon Lexicon XMLLexicon)
           (simplenlg.realiser.english Realiser)))

(declare gen modify!)

(def lexicon (Lexicon/getDefaultLexicon))

(defn modify!
  ([phrase object]
   (modify! phrase object (atom object)))
  ([phrase object object-ref]
   (let [factory (.getFactory object)]
     (match [(:* phrase)]
       [[:pre modifier]]   (snlg/element-add-pre-modifier object modifier)
       [[:front modifier]] (snlg/element-add-front-modifier object modifier)
       [[:post  modifier]] (snlg/element-add-post-modifier object modifier)
       [([& modifiers] :seq)]
         (doseq [modifier modifiers]
           (modify! {:* modifier} object object-ref))
       [{:complement complement}]
         (snlg/element-add-complement object (gen factory complement))
       [{:> (:or :verb :noun :subject :object :clause)}]
         (if (instance? CoordinatedPhraseElement @object-ref)
           (snlg/element-add-coordinate @object-ref (gen factory (:* phrase)))
           (let [cont (gen factory (:* phrase))
                 phrase (snlg/factory-create-coordinated-phrase factory object cont)]
             (reset! object-ref phrase)))
       [{:feature [kind ident]}]
         (let [[feature spec] (feature kind ident)]
           (snlg/element-set-feature @object-ref feature spec))
       [:plural]
         (let [[feature spec] (feature :plural :numbers)]
           (snlg/element-set-feature @object-ref feature spec))
       [modifier] (snlg/element-add-modifier object modifier))
     @object-ref)))

(defmulti modify (fn [phrase object] (:> phrase)))
(defmethod modify :default [phrase object] object)
(defmethod modify :noun [phrase object]
  (if (:* phrase) (modify! phrase object) object))
(defmethod modify :verb [phrase object]
  (if (:* phrase) (modify! phrase object) object))
(defmethod modify :clause [phrase object]
  (if (:* phrase) (modify! phrase object) object))
(defmethod modify :prepophrase [phrase object]
  (if (:* phrase) (modify! phrase object) object))

(defn- noun [phrase factory]
  (match [phrase]
    [[determiner noun]] (snlg/factory-create-noun-phrase factory determiner noun)
    [noun] (snlg/factory-create-noun-phrase factory noun)))

(defn- prepophrase [phrase factory]
  (match [phrase]
    [[preposition complement]] (snlg/factory-create-preposition-phrase
                                 factory
                                 preposition
                                 (gen factory complement))
    [preposition] (snlg/factory-create-preposition-phrase factory preposition)))

(defmulti gen (fn [factory phrase] (:> phrase)))
(defmethod gen :default [factory phrase] phrase)
(defmethod gen :noun    [factory phrase]
  (modify phrase (noun (:+ phrase) factory)))
(defmethod gen :verb    [factory phrase]
  (modify phrase (snlg/factory-create-verb-phrase factory (:+ phrase))))
(defmethod gen :subject [factory phrase]
  (gen factory (assoc phrase :> :noun)))
(defmethod gen :object  [factory phrase]
  (gen factory (assoc phrase :> :noun)))
(defmethod gen :prepophrase [factory phrase]
  (modify phrase (prepophrase (:+ phrase) factory)))

(defmethod gen :clause [factory phrases]
  (let [clause (snlg/factory-create-clause factory)]
    (doseq [phrase (:+ phrases)]
      (condp = (:> phrase)
        :subject (snlg/phrase-set-subject clause (gen factory phrase))
        :verb    (snlg/phrase-set-verb    clause (gen factory phrase))
        :object  (snlg/phrase-set-object  clause (gen factory phrase))
        :complement (snlg/element-add-complement clause (gen factory (:+ phrase)))))
    (modify phrases clause)))

(defmethod gen :generator [name lexicon]
  (let [factory (NLGFactory. (:+ lexicon))
        realiser (Realiser. (:+ lexicon))]
    {:> :generator
     :name name
     :factory factory
     :realiser realiser
     :lexicon (:+ lexicon)
     :* (partial gen factory)
     :! #(snlg/realise-sentence realiser (gen factory %))
     :!! #(snlg/realise-element realiser (gen factory %))}))

(defn make-gen [& [lexicon- name]]
  (let [id (or name (str (java.util.UUID/randomUUID)))]
    (gen id {:> :generator :+ (or lexicon- lexicon)})))
