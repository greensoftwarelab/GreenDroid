from django.urls import path


from repoApp.views.appRelated import ClassesListView,ClassMetricsView

urlpatterns = [
    path('', ClassesListView.as_view(), name='clas'),
    path('metrics/', ClassMetricsView.as_view(), name='clasmet'),
]