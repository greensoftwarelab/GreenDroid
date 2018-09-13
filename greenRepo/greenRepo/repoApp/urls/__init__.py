from django.urls import path, include
from repoApp.views.populate import PopulateView, PopulateDummyTest

urlpatterns = [
	path('projects/', include('repoApp.urls.projects')),
    path('results/', include('repoApp.urls.results')),
    path('tests/', include('repoApp.urls.tests')),
    path('devices/', include('repoApp.urls.devices')),
    path('apps/', include('repoApp.urls.apps')),
    path('methods/', include('repoApp.urls.methods')),
    path('classes/', include('repoApp.urls.classes')),
    path('populate/', PopulateView.as_view(), name='populate'),
    path('populateTest/', PopulateDummyTest.as_view(), name='pop'),
]