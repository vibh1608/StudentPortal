# -*- coding: utf-8 -*-
from __future__ import unicode_literals
from django.shortcuts import render
# My Imports :
from forumapp.forms import CommentForm,QuestionForm
from django.views.generic import (TemplateView,ListView
                                 ,DeleteView,DetailView
                                 ,UpdateView,CreateView,)
from forumapp.models import Comment,Question
from django.contrib.auth.mixins import LoginRequiredMixin
from django.core.urlresolvers import reverse_lazy
from django.shortcuts import get_object_or_404,redirect
from django.contrib.auth.decorators import login_required


# QUESTION CRUD
class QuestionListView(LoginRequiredMixin,ListView):
    model = Question

    def get_queryset(self):
        return Question.objects.all()


class QuestionDetailView(LoginRequiredMixin,DetailView):
    model = Question


class QuestionCreateView(LoginRequiredMixin,CreateView):
    model = Question
    form_class = QuestionForm
    redirect_field_name = "forumapp/question_form.html"
    #     login_url = '/login/'
    # Newly added

    def form_valid(self, form):
        self.object = form.save(commit=False)
        self.object.author = self.request.user
        self.object.save()
        return super(QuestionCreateView, self).form_valid(form)


class QuestionUpdateView(LoginRequiredMixin,UpdateView):
    model = Question
    form_class = QuestionForm
    success_url = reverse_lazy("forumapp:question_list")
    login_url = '/login/'


class QuestionDeleteView(LoginRequiredMixin,DeleteView):
    model = Question
    success_url = reverse_lazy('forumapp:question_list')


# Comments CRUD


@login_required
def add_comment_to_question(request, pk):
    question = get_object_or_404(Question, pk=pk)
    if request.method == "POST":
        form = CommentForm(request.POST)
        if form.is_valid():
            comment = form.save(commit=False)
            comment.question = question             # assign the comment to the question which he commented
            comment.author_comment = request.user   # assign each comment to the user who commented it ...
            comment.save()
            return redirect('forumapp:add_comment_to_question', pk=question.pk)
    else:
        form = CommentForm()
    return render(request, 'forumapp/comment_form.html', {'form': form})


@login_required
def comment_remove(request, pk):
    comment = get_object_or_404(Comment, pk=pk)
    question_pk = comment.question.pk
    comment.delete()
    return redirect('forumapp:question_detail', pk=question_pk)