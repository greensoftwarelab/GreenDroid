from django.urls import path


from repoApp.views.appRelated import MethodsListView,MethodMetricsView,MethodInvokedListView

urlpatterns = [
    #path('', TestsListView.as_view(), name='testRelated'),
    path('', MethodsListView.as_view(), name='met'),
    path('metrics/', MethodMetricsView.as_view(), name='metmet'),
    path('invoked/', MethodInvokedListView.as_view(), name='metRes'),
]