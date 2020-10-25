import axios from 'axios'

const AXIOS = axios.create({
  baseURL: 'weto5/',
  timeout: 1000
});


export default {
  pollLogin() {
    return AXIOS.get('/pollLogin')
  },
  getCourses() {
    return AXIOS.get('/getJSONCourses')
  },
  getUser() {
    return AXIOS.get('/getUser')
  },
  getRealUser() {
    return AXIOS.get('/getRealUser')
  },
  submitLogin(username, password) {
    return AXIOS.post('/submitLoginJSON', {username, password})
  },
  getCourseTask(db, task, tab) {
    return AXIOS.post('/viewJSONCourseTask', {dbId: db, taskId: task, tabId: tab})
  },
  // Submission APIs
  createSubmission(submitterId, dbId, taskId, tabId) {
    let formData = new FormData();
    formData.set("submitterId", submitterId)
    formData.set("dbId", dbId)
    formData.set("taskId", taskId)
    formData.set("tabId", tabId)
    return AXIOS.post("/createSubmission", formData)
  },
  completeSubmission(submissionId, dbId, taskId, tabId) {
    let formData = new FormData();
    formData.set("submissionId", submissionId)
    formData.set("dbId", dbId)
    formData.set("taskId", taskId)
    formData.set("tabId", tabId)
    return AXIOS.post("/completeSubmission", formData)
  },
  deleteSubmission(submissionId, submitted, dbId, taskId, tabId) {
    let formData = new FormData();
    formData.set("submissionId", submissionId)
    formData.set("submitted", submitted)
    formData.set("dbId", dbId)
    formData.set("taskId", taskId)
    formData.set("tabId", tabId)
    return AXIOS.post("/ajaxDeleteSubmissionConfirm", formData)
  },
  saveQuizAnswer(formData) {
    return AXIOS.post("/saveQuizAnswer", formData)
  },
  getSubmissions(db, task, tab) {
    return AXIOS.post("/getJSONSubmissions", {dbId: db, taskId: task, tabId: tab})
  },
  getDocuments(submissionId, dbId, taskId, tabId) {
    return AXIOS.get("/getJSONDocuments", { params: {submissionId, dbId, taskId, tabId} })
  },
  // Create a document entry for the submission
  addSubmissionFile(fileName, submissionId, dbId, taskId, tabId) {
    return AXIOS.post("/addSubmissionFile", {documentFileFileName: fileName, submissionId, dbId, taskId, tabId})
  },
  // TODO: returns the file contents in response, but doesn't initialize file download
  downloadSubmissionFile(documentId, dbId, taskId, tabId) {
    return AXIOS.get("/downloadDocument", { params: {documentId, dbId, taskId, tabId} })
  },
  // Deletes the actual file and the document entry from the submission
  deleteSubmissionFile(documentId, dbId, taskId, tabId) {
    let formData = new FormData();
    formData.set("documentId", documentId)
    formData.set("dbId", dbId)
    formData.set("taskId", taskId)
    formData.set("tabId", tabId)

    return AXIOS.post("/commitDeleteDocument", formData)
  },
  getSubmission(db, task, tab) {
    return AXIOS.post("/getJSONSubmission", {dbId: db, taskId: task, tabId: tab})
  },
  // Grading APIs
  getJSONNodeGrades(dbId, taskId, tabId) {
    return AXIOS.post("/viewJSONNodeGrades", {dbId, taskId, tabId})
  },
  getJSONStudentLeafGrades(dbId, taskId, tabId) {
    return AXIOS.post("/viewJSONStudentLeafGrades", {dbId, taskId, tabId})
  },
  getJSONGrade(dbId, taskId, tabId) {
    return AXIOS.post("/viewJSONGrade", {dbId, taskId, tabId})
  }
}
