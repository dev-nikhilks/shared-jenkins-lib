import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonBuilder

def call(Map<String, String> inputs){
    def pageContent = getConfluencePageContent(inputs.pageId);
    if(pageContent == null){
        return null
    }
    def currentPageVersion = pageContent.results[0].version.number
    def content = pageContent.results[0].body.storage.value
    def title = pageContent.results[0].title

    def config = [
        'customer': inputs.customer,
        'appVersion': inputs.appVersion,
        'pageVersion': currentPageVersion,
        'androidLink': inputs.androidLink,
        'iosLink': inputs.iosLink,
        'title': title,
        'pageId': inputs.pageId
    ]

    def newRow = buildNewTableRow(config)
    def doc = org.jsoup.Jsoup.parse(content)
    def table = doc.select("table[ac:local-id=${inputs.appId}]").first()
    if (!table) {
        println "Table not found"
        return null
    }
    def tbody = table.select("tbody").first()
    tbody.append(newRow)
    doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml).escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
    println "Output is ${doc.body().html()}"
    updateConfluencePageContent(config, doc.body().html())
}

def getConfluencePageContent(pageId) {
    def cmd = ["curl", "-s", "-u", "${CONF_USERNAME}:${CONF_PASSWORD}", "${env.CONFLUENCE_BASE_URL}/pages?id=${pageId}&body-format=storage"]
    def process = cmd.execute()
    process.waitFor()
    // println "This is it ${process.text}"
    if (process.exitValue() != 0) {
        println "Confluence page($pageId) not fetched, error in response"
        return null
    }
    def response = new JsonSlurper().parseText(process.text)
    if(response.results.isEmpty()){
        println "Confluence page($pageId) not fetched "
        return null
    }
    return response;
}

def updateConfluencePageContent(Map<String, String> inputs, newContent) {
    def jsonBuilder = new JsonBuilder([version: [number: inputs.pageVersion + 1], title: inputs.title, id: inputs.pageId, status: "current", body: [representation: "storage", value: newContent]])
    def cmd = ["curl", "-X", "PUT", "-s", "-u", "${CONF_USERNAME}:${CONF_PASSWORD}", "-H", "Content-Type: application/json", "-d", jsonBuilder.toPrettyString(), "${env.CONFLUENCE_BASE_URL}/pages/${inputs.pageId}"]
    def process = cmd.execute()
    process.waitFor()
    if (process.exitValue() != 0) {
        println "ConfluencePageContent Error"
        return null
    }
    def response = new JsonSlurper().parseText(process.text)
    if(response.results.isEmpty()){
        println "Confluence page($pageId) update not competed"
        return null
    }
    println "Confluence page($pageId) updated successfully"
}

def buildNewTableRow(Map<String, String> inputs) {
    def currentDate = new Date().format("dd/MM/yyyy")
    return """
        <tr>
            <td style="text-align: center; vertical-align: middle;">
                <p style="text-align: center;">${currentDate}</p>
            </td>
            <td style="text-align: center; vertical-align: middle;">
                <p style="text-align: center;">${inputs.customer}</p>
            </td>
            <td style="text-align: center; vertical-align: middle;">
                <p style="text-align: center;">${inputs.appVersion}</p>
            </td>
            <td style="text-align: center; vertical-align: middle;">
                <p style="text-align: center;"><a href="${inputs.androidLink}">Download</a></p>
            </td>
            <td style="text-align: center; vertical-align: middle;">
                <p style="text-align: center;"><a href="${inputs.iosLink}">Download</a></p>
            </td>
        </tr>
    """
}
