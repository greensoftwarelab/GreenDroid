from django.db import models

#class ProjectType(models.Model):
#    project_type_designation = models.CharField(max_length=32,primary_key=True)
#    def save(self, *args, **kwargs):
#        self.project_type_designation = self.project_type_designation.lower()
#        return super(ProjectType, self).save(*args, **kwargs)


class AppPermission(models.Model):
    name = models.CharField(max_length=70, primary_key=True)
    def save(self, *args, **kwargs):
        self.name = self.name.lower()
        return super(AppPermission, self).save(*args, **kwargs)


class AppBuildTool(models.Model):
    build_id = models.CharField(max_length=70, primary_key=True)
    def save(self, *args, **kwargs):
        self.build_id = self.build_id.lower()
        return super(AppBuildTool, self).save(*args, **kwargs)


class Method(models.Model):
    class Meta:
        unique_together = (('method_name', 'method_class','method_hash_args'),)
    method_id = models.CharField(primary_key=True,max_length=300)
    method_name = models.CharField(max_length=256)
    method_class = models.CharField(max_length=128)
    method_hash_args = models.CharField(max_length=34,default="")


class Application(models.Model):
    app_id = models.CharField(max_length=50,primary_key=True)
    app_location= models.CharField(max_length=512)
    app_description = models.CharField(max_length=100,default=None,null=True)
    #app_type = models.ForeignKey(ProjectType, related_name='has_type', on_delete=models.PROTECT)
    app_language = models.CharField(max_length=20)
    app_build_tool = models.ForeignKey(AppBuildTool, related_name='has_type', on_delete=models.PROTECT)
    app_version= models.FloatField(default=1)
    def save(self, *args, **kwargs):
        self.app_language = self.app_language.lower()
        return super(Application, self).save(*args, **kwargs)

class AppHasPermission(models.Model):
    class Meta:
        unique_together = (('application', 'permission'),)
    application = models.ForeignKey(Application, related_name='hasapp', on_delete=models.CASCADE)
    permission = models.ForeignKey(AppPermission, related_name='haspermission', on_delete=models.CASCADE)

class AppHasMethod(models.Model):
    class Meta:
        unique_together = (('application', 'method'),)
    application = models.ForeignKey(Application, related_name='belongstoapp', on_delete=models.CASCADE)
    method = models.ForeignKey(Method, related_name='methodname', on_delete=models.CASCADE)


