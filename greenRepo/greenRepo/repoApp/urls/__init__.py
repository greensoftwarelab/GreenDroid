from django.urls import path, include
from repoApp.views.populate import PopulateView
urlpatterns = [
    path('results/', include('repoApp.urls.results')),
    path('tests/', include('repoApp.urls.tests')),
    path('devices/', include('repoApp.urls.devices')),
    path('apps/', include('repoApp.urls.apps')),
    path('methods/', include('repoApp.urls.methods')),
    path('populate/', PopulateView.as_view(), name='populate'),
]