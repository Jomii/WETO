import Vue from 'vue'
import Router from 'vue-router'
import Task from './views/Task.vue'
import Submissions from './views/Submissions.vue'
import Grading from './views/Grading.vue'
import Forum from './views/Forum.vue'
import Groups from './views/Groups.vue'
import CourseList from './views/CourseList.vue'
import Login from './views/Login.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'login',
      component: Login
    },
    {
      path: '/courses',
      name: 'courseList',
      component: CourseList
    },
    {
      path: '/task',
      name: 'task',
      component: Task
    },
    {
      path: '/grading',
      name: 'grading',
      component: Grading
    },
    {
      path: '/submissions',
      name: 'submissions',
      component: Submissions
    },
    {
      path: '/forum',
      name: 'forum',
      component: Forum
    },
    {
      path: '/groups',
      name: 'groups',
      component: Groups
    },
  ]
})
