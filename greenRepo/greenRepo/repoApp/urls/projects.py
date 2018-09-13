from django.urls import path, re_path


from repoApp.views.appRelated import ProjectListView, ProjectView

urlpatterns = [
    path('', ProjectListView.as_view(), name='projs'),
    path('<slug:projid>/', ProjectView.as_view(), name='proj'),
    
]