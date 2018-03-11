from django.conf.urls import url
from forumapp import views


# I Edited
app_name = "forumapp"
# end I edited

# I Made
urlpatterns = [
    url(r'^$', views.QuestionListView.as_view(), name='question_list'),
    url(r'^question/(?P<pk>\d+)$', views.QuestionDetailView.as_view(), name='question_detail'),
    url(r'^question/new/$', views.QuestionCreateView.as_view(), name='question_new'),
    url(r'^question/(?P<pk>\d+)/edit/$', views.QuestionUpdateView.as_view(), name='question_edit'),
    url(r'^question/(?P<pk>\d+)/remove/$', views.QuestionDeleteView.as_view(), name='question_remove'),

    # Comments :
    url(r'^question/(?P<pk>\d+)/comment/$', views.add_comment_to_question, name='add_comment_to_question'),
    url(r'^comment/(?P<pk>\d+)/remove/$', views.comment_remove, name='comment_remove'),

]