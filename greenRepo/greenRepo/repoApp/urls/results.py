from django.urls import path


from repoApp.views.testRelated import ResultsListView

urlpatterns = [
    path('', ResultsListView.as_view(), name='testRelated'),
    #path('', ResultsListView.as_view(), name='testRelated'),
]