# KnowledgeQuiz
An Android 'trivia' application developed in Android Studio.
## Description
 The user can answer quiz questions from any category, but has the option choose a specific category.
 Regardless to language of device, the questions will be displayed in English.
 ## Specification
* The questions are loaded from [Open Trivia Database](http://opentdb.com). Therefore the application needs internet permission.
* There are 10 questions in one round of gameplay. These question can be true/false or multiple choice.
* Before starting a round the user needs to specifiy the category of questions.
* The application saves the results to a persistently stored database on the device. The user can view the stored results on a specific activity. Of course the database' elements can be deleted one-by-one and all at once.
## Other non-specified features
* A transition is displayed when changing Activities.
### HighScores page
* A result will store:
  * Your name (defined in Settings page)
  * Category
  * Score (good answers out of 10)
  * Time taken 
* The results are displayed in descending order according to score.
* In the Scores page you can search for categories in a search bar.
* You can select to view all results stored or only the best result of each category.
* Each category has its own logo.
* Buttons have rounded corners.
* Transitions/animations:
  * Loading animation (Layout animation)
  * Item deletion animation (DiffUtil)
  * Filtering animation (DiffUtil)
### Settings page
* The application has a Settings page where you can a enter your name. Your name will be stored in your results.

## Future goals
* Night mode
* ...
