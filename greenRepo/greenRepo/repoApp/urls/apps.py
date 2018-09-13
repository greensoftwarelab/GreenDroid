from django.urls import path, re_path


from repoApp.views.appRelated import AppsListView,AppsDetailView,AppsMethodsListView,AppsMethodsDetailView,MethodsListView, AppHasPermissionListView, AppsTestsView , AppsClassListView,AppsMetricsView,AppsTestResultsView

urlpatterns = [
    #path('', TestsListView.as_view(), name='testRelated'),
    re_path('permissions/', AppHasPermissionListView.as_view(), name='appshas'),
    re_path('metrics/', AppsMetricsView.as_view(), name='apps'),
    path('<slug:appid>/', AppsDetailView.as_view(), name='apps'),
    path('<slug:appid>/tests/', AppsTestsView.as_view(), name='apps'),
    path('<slug:appid>/tests/results/', AppsTestResultsView.as_view(), name='apps'),
    path('<slug:appid>/classes/', AppsClassListView.as_view(), name='clas'),
    path('<slug:appid>/methods/', AppsMethodsListView.as_view(), name='apps'),
    path('<slug:appid>/methods/<slug:methodid>/', AppsMethodsDetailView.as_view(), name='apps'),
    path('', AppsListView.as_view(), name='apps'),
    #path('methods/', MethodsListView.as_view(), name='apps'),
    #path('', ResultsListView.as_view(), name='testRelated'),
]