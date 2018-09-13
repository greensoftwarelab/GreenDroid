from django.db import models
from enumfields import EnumField, Enum
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



class AndroidProject(models.Model):
    project_id = models.CharField(max_length=64, primary_key=True)
    project_build_tool=  models.CharField(max_length=16, default="gradle",blank=True)
    project_desc = models.CharField(max_length=64,default="",blank=True)



class Application(models.Model):
    app_id = models.CharField(max_length=128,primary_key=True)
    app_location= models.CharField(max_length=512)
    app_description = models.CharField(max_length=64,default=None,null=True)
    app_version= models.FloatField(default=1)
    app_flavor= models.CharField(max_length=32, default="")
    app_build_type = models.CharField(max_length=16,default="",blank=True)
    app_project = models.ForeignKey(AndroidProject, related_name='belongs_project', on_delete=models.CASCADE)

class Class(models.Model):
    class_id = models.CharField(primary_key=True,max_length=96)
    class_name = models.CharField(max_length=32)
    class_package = models.CharField(max_length=32)
    class_non_acc_mod = models.CharField(max_length=32, default=None,blank=True, null=True)
    class_app = models.ForeignKey(Application, related_name='belongs_app', on_delete=models.CASCADE)
    class_acc_modifier = models.CharField(max_length=16, default=None,blank=True, null=True )
    class_superclass = models.CharField(max_length=64, default=None,blank=True, null=True)
    class_is_interface = models.BooleanField(default=False)
    #class_n_vars = models.IntegerField()
    class_implemented_ifaces = models.CharField(max_length=256,default="",blank=True)

class ImportClass(models.Model):
    class Meta:
        unique_together = (('import_name', 'import_class'),)
    import_name = models.CharField(max_length=64, primary_key=True)
    import_class= models.ForeignKey(Class, related_name='classofimport', on_delete=models.CASCADE, null=False)
    def save(self, *args, **kwargs):
        self.import_name = self.import_name.lower()
        return super(ImportClass, self).save(*args, **kwargs)



class Method(models.Model):
    class Meta:
        unique_together = (('method_name', 'method_class','method_id'),)
    method_id = models.CharField(primary_key=True,max_length=384)
    method_name = models.CharField(max_length=256)
    #method_hash_args = models.CharField(max_length=34,default="")
    method_non_acc_mod = models.CharField(max_length=32,default="",blank=True)
    method_acc_modifier = models.CharField(max_length=16,default="",blank=True)
    method_class = models.ForeignKey(Class, related_name='classofmethod', on_delete=models.CASCADE, null=True)
    #method_args = models.CharField(max_length=256, default = None, null=True)



class AppHasPermission(models.Model):
    class Meta:
        unique_together = (('application', 'permission'),)
    application = models.ForeignKey(Application, related_name='hasapp', on_delete=models.CASCADE)
    permission = models.ForeignKey(AppPermission, related_name='haspermission', on_delete=models.CASCADE)

