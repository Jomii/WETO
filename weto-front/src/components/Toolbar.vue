<template>
  <nav>
    <v-app-bar app dark dense class="primary" :collapse=appbarCollapse>
      <v-btn v-if="!sidebar" icon dark @click="sidebar = !sidebar">
        <v-icon>menu</v-icon>
      </v-btn>
      <v-btn v-else icon dark disabled>
        <v-icon>menu</v-icon>
      </v-btn>

      <v-btn icon @click="appbarCollapse = !appbarCollapse">
        <v-icon v-if="appbarCollapse === false">chevron_left</v-icon>
        <v-icon v-else>chevron_right</v-icon>
      </v-btn>
      <v-toolbar-items v-if="courseSelected() && !appbarCollapse">
        <v-btn text class="hidden-sm-and-down" router to="/task">
          <v-icon left>school</v-icon>
          <span>MAIN</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/grading">
          <v-icon left>grade</v-icon>
          <span>GRADING</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/submissions">
          <v-icon left>subject</v-icon>
          <span>SUBMISSIONS</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/forum">
          <v-icon left>forum</v-icon>
          <span>FORUM</span></v-btn>
        <v-btn text class="hidden-sm-and-down" router to="/groups">
          <v-icon left>group</v-icon>
          <span>GROUPS</span></v-btn>

        <v-btn text fab class="hidden-md-and-up" router to="/task">
          <v-icon>home</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/grading">
          <v-icon>grade</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/submissions">
          <v-icon>subject</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/forum">
          <v-icon>forum</v-icon>
        </v-btn>
        <v-btn text fab class="hidden-md-and-up" router to="/groups">
          <v-icon>group</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <v-navigation-drawer
        app
        light
        floating
        class="info"
        v-model=sidebar>
      <v-app-bar dark dense class="primary">
        <v-btn icon v-if="sidebar" @click="sidebar = !sidebar" dark>
          <v-icon>clear</v-icon>
        </v-btn>
      </v-app-bar>

      <v-list flat>
        <v-list-item-group>
          <v-list-item link v-if="courseName !== ''" router to="/">
            <v-list-item-icon>
              <v-icon>home</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>
                WETO
              </h3>
            </v-list-item-content>
          </v-list-item>
          <v-list-item link v-else>
            <v-list-item-icon>
              <v-icon>home</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>
                WETO
              </h3>
            </v-list-item-content>
          </v-list-item>

          <v-list-item link>
            <v-list-item-icon>
              <v-icon>face</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>
                {{ this.$store.getters.user.firstNameData.value }}
                {{ this.$store.getters.user.lastNameData.value }}
              </h3>
            </v-list-item-content>
          </v-list-item>

          <v-list-item v-if="courseName !== ''" @click="selectRootTask">
            <v-list-item-icon>
              <v-icon>info</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <h3>{{ courseName }}</h3>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>

      <v-treeview :items="subTasks">
        <template slot="label" slot-scope="{ item }">
          <a @click="selectSubTask(item)">{{ item.name }}</a>
        </template>
      </v-treeview>

      <v-list class="pa-0" dense v-if="courseSelected()">
        <v-list-item v-for="link in links" :key="link.text" router :to="link.route">
          <v-list-item-content>
            <span>
              {{ link.text }}
            </span>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
  </nav>
</template>

<script>

  export default {
    name: "Toolbar.vue",
    data() {
      return {
        sidebar: true,
        appbarCollapse: false,
        links: [
          {icon: 'exit_to_app', text: 'Return to course list', route: '/', toolbar: false}
        ]
      }
    },
    computed: {
      props() {
        return {
          appbarCollapse: {collapse: false}
        }
      },
      courseName: function () {
        return this.$store.getters.selectedCourse.name;
      },
      subTasks: function () {
        return this.$store.getters.subTaskTree;
      }
    },
    methods: {
      courseSelected() {
        return this.$store.getters.selectedCourse.courseTaskId !== null &&
          this.$store.getters.selectedCourse.databaseId !== null;
      },
      selectRootTask() {
        this.$store.commit("setTask", this.$store.getters.selectedCourse.courseTaskId);
      },
      selectSubTask(item) {
        this.$store.commit("setTask", item.id);
        this.$router.replace('/task');
      }
    }
  }

</script>