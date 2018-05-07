from django.urls import path, re_path


from repoApp.views.appRelated import AppsListView,AppsDetailView,AppsMethodsListView,AppsMethodsDetailView,MethodsListView, AppHasPermissionListView

urlpatterns = [
    #path('', TestsListView.as_view(), name='testRelated'),
    re_path('permissions/', AppHasPermissionListView.as_view(), name='appshas'),
    path('<slug:appid>/', AppsDetailView.as_view(), name='apps'),
    path('<slug:appid>/results/', AppsDetailView.as_view(), name='apps'),
    path('<slug:appid>/methods/', AppsMethodsListView.as_view(), name='apps'),
    path('<slug:appid>/methods/<int:methodid>/', AppsMethodsDetailView.as_view(), name='apps'),
    path('', AppsListView.as_view(), name='apps'),
    #path('methods/', MethodsListView.as_view(), name='apps'),
    #path('', ResultsListView.as_view(), name='testRelated'),
]