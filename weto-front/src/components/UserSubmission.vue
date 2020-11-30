<template>
  <div>
    <v-simple-table>
      <template v-slot:default>
        <thead>
          <tr>
            <th class="text-left">Name</th>
            <th class="text-left">Date modified</th>
            <th class="text-left">File size</th>
            <th class="text-left">Status</th>
            <th class="text-left">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="doc in documents" :key="doc.id">
            <td>{{ doc.fileName }}</td>
            <td>{{ doc.fileDate }}</td>
            <td>{{ doc.fileSize }} bytes</td>
            <td>{{ getSubmissionStatus }}</td>
            <td>
              <v-row>
                <v-btn
                  depressed
                  class="docAction"
                  color="primary"
                  @click="downloadSubmissionFile(doc.fileName, doc.id)"
                >
                  <v-icon>download</v-icon>
                  Download
                </v-btn>
                <!-- <v-btn depressed color="secondary" class="docAction">Edit</v-btn> -->
                <template>
                  <v-btn
                    depressed
                    class="docAction"
                    color="error"
                    @click="showDeleteDialog(doc)"
                  >
                    <v-icon>delete</v-icon>
                    Delete
                  </v-btn>
                </template>
              </v-row>
            </td>
          </tr>
        </tbody>
      </template>
    </v-simple-table>

    <v-dialog
      v-if="submissionPeriodActive === true"
      v-model="dialog"
      max-width="400"
    >
      <v-card>
        <v-card-title>
          Delete file from submission
        </v-card-title>
        <v-card-text>
          Are you sure you want to delete the file:
          {{ documentToDelete.fileName }}?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="secondary" text @click="dialog = false">Cancel</v-btn>
          <v-btn
            color="primary"
            text
            @click="
              dialog = false;
              deleteSubmissionFile(documentToDelete.id);
            "
            >Delete</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import api from "../backend-api";
export default {
  name: "userSubmission",
  data() {
    return {
      backendResponse: [],
      errors: [],
      dialog: false,
      documentToDelete: {},
    };
  },
  props: {
    submissionStatus: String,
    submission: Object,
    documents: Array,
    submissionPeriodActive: Boolean,
  },
  computed: {
    taskId() {
      return this.$store.getters.currentTask;
    },
    dbId() {
      return this.$store.getters.selectedCourse.databaseId;
    },
    tabId() {
      return 4;
    },
    taskName() {
      return this.$store.getters.selectedCourse.name;
    },
    getSubmissionStatus() {
      return this.submissionStatus === "Accepted"
        ? "Submitted"
        : this.submissionStatus;
    },
  },
  methods: {
    downloadSubmissionFile(filename, id) {
      api.downloadSubmissionFile(
        filename,
        id,
        this.dbId,
        this.taskId,
        this.tabId
      );
    },
    deleteSubmissionFile(id) {
      api
        .deleteSubmissionFile(id, this.dbId, this.taskId, this.tabId)
        .then(() => {
          this.$emit("deleteDocument");
        })
        .catch((err) => {
          console.log(err);
          this.backendResponse.push(err);
        });
    },
    showDeleteDialog(document) {
      this.documentToDelete = document;
      this.dialog = !this.dialog;
    },
  },
};
</script>

<style scoped>
.row {
  align-content: center;
  justify-content: flex-start;
}
.v-data-table {
  margin: 1em;
  border: 1px #102027 solid;
}
thead {
  background-color: #f3e5ff;
}
.docAction {
  margin-right: 2em;
  margin-top: 0.5em;
  margin-bottom: 0.5em;
  padding-left: 12px;
}
</style>
