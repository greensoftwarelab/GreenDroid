from django.urls import path


from repoApp.views.testRelated import TestResultsListView,TestsListView,ResultsListView,ResultsTestListView,TestMetricsListView

urlpatterns = [
    path('', TestsListView.as_view(), name='testRelated'),
    path('results/', TestResultsListView.as_view(), name='testRelated'),
    path('<int:testid>/results/', ResultsTestListView.as_view(), name='testRelated'),
    path('metrics/', TestMetricsListView.as_view(), name='testRelated'),
    #path('', ResultsListView.as_view(), name='testRelated'),
]