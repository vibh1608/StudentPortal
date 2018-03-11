from __future__ import unicode_literals
from django.utils.encoding import python_2_unicode_compatible
import uuid
from django.db import models
from django.conf import settings


class BaseProfile(models.Model):
    user = models.OneToOneField(settings.AUTH_USER_MODEL,
                                primary_key=True)
    slug = models.UUIDField(default=uuid.uuid4, blank=True, editable=False)
    # Add more user profile fields here. Make sure they are nullable
    # or with default values
    picture = models.ImageField('Profile picture',
                                upload_to='profile_pics/%Y-%m-%d/',
                                null=True,
                                blank=True)
    bio = models.CharField("Short Bio", max_length=200, blank=True, null=True)
    email_verified = models.BooleanField("Email verified", default=False)

    class Meta:
        abstract = True


class Course(models.Model):
    course_id = models.CharField(max_length=5,unique=True)      # Made Unique
    course_name = models.CharField(max_length=25,unique=True)   # Made Unique

    def __str__(self):
        return self.course_id

    def __unicode__(self):
        return self.course_id


DEPART_CHOICE = ((u'coe', u'COE'),
                 (u'mdm', u'MDM'),
                 (u'edm', u'EDM'))


@python_2_unicode_compatible
class Profile(BaseProfile):
    depart = models.CharField(max_length=3, choices=DEPART_CHOICE, default='coe')
    # course = models.CharField(max_length=20,null=True,blank=True)
    course = models.ManyToManyField(Course)
    contact = models.BigIntegerField(default=None, null=True, blank=True)
    resume = models.FileField('Teacher Resume', upload_to='resume', null=True, blank=True)

    # Link
    github = models.URLField(max_length=100, blank=True, null=True)
    linkedin = models.URLField(max_length=100, blank=True, null=True)
    twitter = models.URLField(max_length=100, blank=True, null=True)

    def __str__(self):
        return "{}'s profile". format(self.user)





