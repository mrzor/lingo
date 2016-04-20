(ns lingo.simplenlg-wrapper
  (:import (simplenlg.framework NLGElement PhraseElement NLGFactory CoordinatedPhraseElement)
           (simplenlg.realiser.english Realiser)))

; Most SimpleNLG functions that lingo calls are wrapped here
;
; My only reason not to implement this as a macro is to allow for easier
; debugging / breakpointing / profiling at the cost of extra stack action.
; It also allows tracing using tools.trace
;
; I wish a dynamic Java proxy could do the same job, but they appear to be
; limited to interfaces.
;
; See comments in
; http://stackoverflow.com/questions/36534788/log-all-method-calls-to-a-java-object-from-clojure
;
; This could probably all replaced, someday, with a far better solution.
; I regret polluting lingo with this, but I see no other options to help me pinpoint
; some bugs where a same lingo and simpleNLG test case differ in output.

(defn realise-sentence [^Realiser realiser ^NLGElement element]
  (.realiseSentence realiser element))

(defn realise-element [^Realiser realiser ^NLGElement element]
  (.getRealisation (.realise realiser element)))

(defn factory-create-sentence [^NLGFactory factory]
  (.createSentence factory))

(defn factory-create-clause  [^NLGFactory factory]
  (.createClause factory))

(defn factory-create-coordinated-phrase [^NLGFactory factory first-phrase second-phrase]
  (.createCoordinatedPhrase factory first-phrase second-phrase))

(defn factory-create-noun-phrase
  ([^NLGFactory factory noun] (.createNounPhrase factory noun))
  ([^NLGFactory factory specifier noun] (.createNounPhrase factory specifier noun)))

(defn factory-create-preposition-phrase
  ([^NLGFactory factory preposition]
   (.createPrepositionPhrase factory preposition))

  ([^NLGFactory factory preposition complement]
   (.createPrepositionPhrase factory preposition complement)))

(defn factory-create-verb-phrase [^NLGFactory factory verb]
  (.createVerbPhrase factory verb))

(defn element-add-pre-modifier [^NLGElement element modifier]
  ; type hint is slightly off : NLGElement doesnt have addPreModifier, but several descendants
  ; of it have it, and there isn't any interface.
  (.addPreModifier element modifier))

(defn element-add-front-modifier [^NLGElement element modifier]
  ; type hint is slightly off : same as add-pre-modifier
  (.addFrontModifier element modifier))

(defn element-add-post-modifier [^NLGElement element modifier]
  ; type hint is slightly off : same as add-pre-modifier
  (.addPostModifier element modifier))

(defn element-add-modifier [^NLGElement element modifier]
  ; type hint is slightly off : same as add-pre-modifier
  (.addModifier element modifier))

(defn element-add-complement [^NLGElement element complement]
  ; type hint is slightly off : same as add-pre-modifier
  (.addComplement element complement))

(defn element-add-coordinate [^CoordinatedPhraseElement element coordinate]
  (.addCoordinate element coordinate))

(defn element-set-feature [^NLGElement element feature-key feature-val]
  (.setFeature element feature-key feature-val))

(defn element-set-determiner [^PhraseElement element determiner]
  (.setDeterminer element determiner))

(defn phrase-set-subject [phrase subject]
  (.setSubject phrase subject))

(defn phrase-set-verb-phrase [phrase verb]
  (.setVerbPhrase phrase verb))

(defn phrase-set-object [phrase object]
  (.setObject phrase object))