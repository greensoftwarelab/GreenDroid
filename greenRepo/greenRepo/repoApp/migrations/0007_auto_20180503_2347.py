# Generated by Django 2.0.4 on 2018-05-03 23:47

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('repoApp', '0006_auto_20180503_2346'),
    ]

    operations = [
        migrations.AlterField(
            model_name='application',
            name='app_version',
            field=models.FloatField(default=1),
        ),
    ]
