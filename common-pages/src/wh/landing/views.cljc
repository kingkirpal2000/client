;; Candidate landing page

(ns wh.landing.views
  (:require
    #?(:cljs [wh.pages.core :refer [load-and-dispatch]])
    [wh.common.data :as data]
    [wh.components.cards :refer [blog-card]]
    [wh.components.carousel :refer [carousel]]
    [wh.components.common :refer [companies-section link wrap-img img]]
    [wh.components.www-homepage :as www :refer [animated-hr]]
    [wh.how-it-works.views :as hiw]
    [wh.interop :as interop]
    [wh.landing.subs :as subs]
    [wh.re-frame.events :refer [dispatch]]
    [wh.re-frame.subs :refer [<sub]]
    [wh.routes :as routes]
    [wh.util :as util]))

(def issues-benefits
  [{:img "/images/hiw/candidate/benefits/benefit1.svg"
    :txt "Learn new languages and add new skills to your resume"}
   {:img "/images/hiw/candidate/benefits/benefit2.svg"
    :txt "Get paid to contribute to projects you find interesting"}
   {:img "/images/hiw/candidate/benefits/benefit3.svg"
    :txt "Learn about a company’s stack and culture before even applying"}
   {:img "/images/hiw/candidate/benefits/benefit4.svg"
    :txt "Get Fast-tracked for full time jobs at companies you’ve already worked for"}])

(def how-we-are-different-items
  [{:img "/images/homepage/different1.svg"
    :title "We know our stuff"
    :txt "Unlike other companies, we know the difference between Java and Javascript and won’t ask for 6 years experience in a language that came out last year."}
   {:img "/images/hiw/company/benefits/benefit4.svg"
    :title "We don’t waste your time"
    :txt "Our specialist platform unique algorithm means that we only send you jobs you’re actually a match for. You’ll only get emails for jobs you can actually get."}
   {:img "/images/hiw/company/benefits/benefit1.svg"
    :title "We’re the future of tech work"
    :txt "With our Open Source issues, you can get paid, learn new languages and get noticed by employers. We’re leveraging the power of open source to help you further your career."}])

(defn oauth-github-button []
  [:a.button.button--public.button--github.button--github-integration
   {:href (routes/path :login :params {:step :github})}
   [:span "Get Started with"] [:div]])

(defn job-card
  [{:keys [company tagline title display-salary display-location role-type tags id slug] :as job}]
  (let [{company-name :name logo :logo} company
        skeleton? (and job (empty? (dissoc job :id)))]
    [:div {:class (util/merge-classes "card"
                                      "card--job"
                                      (str "i-cur-" (rand-int 9))
                                      (when skeleton? "job-card--skeleton"))}
     [:span
      [:div.info
       [:div.logo
        (if skeleton?
          [:div]
          [link (wrap-img img logo {:alt (str company-name " logo") :w 48 :h 48}) :job :slug slug])]
       [:div.basic-info
        [:div.job-title [link title :job :slug slug]]
        [:div.company-name [link company-name :job :slug slug]]
        [:div.location [link display-location :job :slug slug]]
        (when-not (= role-type "Full time")
          [:div.role-type role-type])]
       [:div.salary display-salary]]]
     (into [:ul.tags.tags__job]
           (map (fn [tag] [:li [link tag :jobsboard :query-params {"tags" tag}]])
                (if skeleton?
                  (map (fn [_i] (apply str (repeat (+ 8 (rand-int 30)) " ")))
                       (range 6))
                  tags)))
     [:div.tagline tagline]
     [:div.buttons
      [link [:button.button.button--inverted "More Info"] :job :slug slug]
      [:button.button
       (interop/on-click-fn
         (interop/show-auth-popup :homepage-jobcard-apply
                                  [:job
                                   :params {:slug slug}
                                   :query-params {:apply "true"}]))
       "Apply"]]]))

(defn header []
  (let [{:keys [discover]} (get data/in-demand-hiring-data (<sub [:wh/vertical]))]
    [:div.homepage__header
     [:div.homepage__header__img
      [:div.homepage__header__img-inner
       [:img {:src "/images/homepage/header.svg"
              :alt ""}]]]
     [:div.homepage__header__copy
      [:h1 discover]
      [:p "We match your skills to great jobs using languages you love."]
      [:div.landing__header__buttons
       [oauth-github-button]
       (link [:button.button
              {:id "www-landing__hero"}
              "Get Started"] :get-started)]]]))

(defn bottom []
  (let [{:keys [title href]} (get data/in-demand-hiring-data (<sub [:wh/vertical]))]
    [:div.homepage__bottom-content.homepage__looking-to-hire--candidate
     [:div.homepage__bottom-content__container
      [:div.homepage__looking-to-hire
       [:img {:src "/images/homepage/feature02.svg"
              :alt ""}]
       [:div.homepage__looking-to-hire__header
        [:h3 "Looking to hire " title "?"]
        [:p "Whether you’re looking to hire software developers or engineers, from front-end to full-stack to back-end, we’ve got you covered"]
        [:div.homepage__feature-ctas
         [:a.button.button--inverted {:href (str "https://www.works-hub.com" href)} "Start hiring"]]]]]]))

(defn candidate-page []
  [:div.homepage.landing-page
   [:div.homepage__top-content
    [:div.homepage__top-content__container
     (header)]
    [:div.homepage__companies-section
     [:div.homepage__companies-section__container
      (companies-section "Join engineers from:")]]]
   [:div.homepage__middle-content
    (let [jobs (<sub [::subs/jobs])]
      [:div.homepage__middle-content__container.homepage__jobs-container
       [:h2 "BROWSE ROLES AT THE TOP TECH COMPANIES"]
       [:h3 "See who’s hiring"]
       [:div.homepage__jobs.columns.is-hidden-mobile
        (for [job jobs]
          ^{:key (:id job)}
          [:div.column [job-card job]])]
       [:div.is-hidden-desktop
        (let [blogs (<sub [::subs/blogs])]
          [carousel (for [job jobs] [job-card job])])]
       [:div.homepage__feature-ctas
        (link [:button.button
               "View All Jobs"] :jobsboard)]])
    [animated-hr "/images/homepage/rocket.svg" "homepage__animated-hr__rocket"]
    [:div.homepage__middle-content__container
     [:h2 "COLLABORATE AND IMPROVE YOUR SKILLSET"]
     [:h3 "Open source issues"]
     (hiw/benefits-list issues-benefits {:class "homepage__benefits-list"})
     [:div.homepage__feature-ctas
      (link [:button.button.button--inverted "How It Works"] :how-it-works)
      (link [:button.button "View All Open Source Issues"] :issues)]]
    [animated-hr nil nil]
    [:div.homepage__middle-content__container.homepage__learn.columns
     [:div.column.homepage__blog-carousel-container
      (let [blogs (<sub [::subs/blogs])]
        [carousel (for [blog blogs] [blog-card blog])])
      [:div.homepage__feature-ctas.is-hidden-desktop
       (link [:button.button "View All Articles"] :learn)]]
     [:div.column
      [:h2 "LEARN AND CONTRIBUTE"]
      [:h3 "Articles that matter to you"]
      [:p "We tap into the brains of the world’s most talented Functional programmers to bring you fresh thinking and unique perspectives."]
      [:div.homepage__feature-ctas.is-hidden-mobile
       (link [:button.button "View All Articles"] :learn)]]]
    [animated-hr "/images/homepage/globe.svg" "homepage__animated-hr__globe"]
    [:div.homepage__middle-content__container
     [:h2 "AND FINALLY…"]
     [:h3 "How we’re different"]
     (hiw/benefits-list how-we-are-different-items {:class "homepage__benefits-list homepage__how-we-are-different"})
     [:div.homepage__feature-ctas
      [oauth-github-button]
      (link [:button.button
             {:id "www-landing__hero"}
             "Get Started"] :get-started)]]]
   [bottom]])

(defn page []
  (cond
    (= "www" (<sub [:wh/vertical]))
    (www/homepage (<sub [:wh/page-params]))

    true
    (candidate-page)))
