from django.urls import path


from repoApp.views.devices import DevicesListView

urlpatterns = [
    #path('', TestsListView.as_view(), name='testRelated'),
    path('', DevicesListView.as_view(), name='devices'),
    #path('', ResultsListView.as_view(), name='testRelated'),
]