# -*- coding: utf-8 -*-
from __future__ import unicode_literals
from django.db import models
from profiles.models import BaseProfile
from django.conf import settings
# My Imports

from django.core.urlresolvers import reverse


# class TweetManager(models.Manager):
#
#     def like_toggle(self, user, tweet_obj):
#         if user in tweet_obj.liked.all():
#             is_liked = False
#             tweet_obj.liked.remove(user)
#         else:
#             is_liked = True
#             tweet_obj.liked.add(user)
#         return is_liked


class Question(models.Model):
    # Cannot DO ? ????????????????????????????????
    author = models.ForeignKey(settings.AUTH_USER_MODEL)
    title = models.CharField(max_length=100)
    content = models.CharField(max_length=1000,null=True)
    # liked = models.ManyToManyField(Profile,blank=True,related_name='liked')
    timestamp = models.DateTimeField(auto_now_add=True)

    # is_published = models.BooleanField(default=False)

    def __str__(self):
        return self.title

    def __unicode__(self):
        return self.title

    def get_absolute_url(self):
        return reverse('forumapp:question_new')
#        # return reverse('forumapp:question_list')
        #  Changed to redirect the website after
        # submitting the question form to the same form
        # ... so that iframe can be used

    class Meta:
        ordering = ['-timestamp']


class Comment(models.Model):
    question = models.ForeignKey(Question,related_name='comments')
    author_comment = models.ForeignKey(settings.AUTH_USER_MODEL,default=None)
    content = models.CharField(max_length=1000,null=True,blank=False)
    is_valid = models.BooleanField(default=False)
    timestamp_comment = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.content

    def __unicode__(self):
        return self.content

    class Meta:
        ordering = ['-timestamp_comment']