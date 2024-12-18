import groovy.json.JsonSlurper
import groovy.json.JsonOutput

// Function to set the values to confluence
def call(pageId, project, releaseVersion, type, android, ios){
    println "Confluence Start Fetch"
    def pageData = fetchPageContent(pageId);
    println "Confluence Initial Fetch"
    if( pageData != null ) {
        def currentVersion = pageData.results[0].version.number
        def pageTitle = pageData.results[0].title
        def pageBody = pageData.results[0].body.storage.value

        def updatedBody = appendRowToSpecificTable(pageBody, project, releaseVersion, type, android, ios)
        // updateConfluencePage(pageId, pageTitle, pageBody, currentVersion)
        println "Confluence Success"
    } else {
        println "Confluence Page Not found"
    }
}

// Function to fetch the current page content
def fetchPageContent(pageId) {
    def url = "${env.CONFLUENCE_BASE_URL}/pages?id=${pageId}&body-format=storage"
    def connection = new URL(url).openConnection()
    connection.setRequestProperty("Authorization", "Basic " + "${CONF_USERNAME}:${CONF_PASSWORD}".bytes.encodeBase64().toString())
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestMethod("GET")
    
    try {
        def responseCode = connection.responseCode
        if (responseCode == 200) {
            def response = connection.inputStream.text
            return new JsonSlurper().parseText(response)
        } else {
            def errorMessage = connection.errorStream?.text ?: "Unknown error"
            println "Failed to fetch Confluence page content. HTTP ${responseCode}: ${errorMessage}"
            return null
        }
    } catch (Exception e) {
        println "Error occurred while fetching Confluence page content: ${e.message}"
        return null
    }
}

// Function to append a row to a specific table identified by ac:local-id
def appendRowToSpecificTable(bodyContent, targetTableId, version, customer, androidLink, iosLink) {
    def newRow = buildNewTableRow(version, customer, androidLink, iosLink)

    // // Locate the target table using ac:local-id
    // def updatedBody = bodyContent.replaceFirst(/(<table[^>]*ac:local-id="${targetTableId}"[^>]*>.*?<tbody>)(.*?)(<\/tbody>)/) { match, beforeBody, rows, afterBody ->
    //     "${beforeBody}${rows}${newRow}${afterBody}"
    // }


 
    // if (updatedBody == bodyContent) {
    //     error "No table with ac:local-id '${targetTableId}' was found."
    // }
    def doc = org.jsoup.Jsoup.parse(bodyContent)
    def table = doc.select("table[ac\\:local-id=\"${targetTableId}\"]").first()
    if (!table) {
        return null
    }
    table.append("${newRow}")
    println "Confluence page data is ${doc.html()}"
    return null
}

// Function to build a new table row
def buildNewTableRow(version, customer, androidLink, iosLink) {
    def currentDate = new Date().format("dd/MM/yyyy")
    return """
        <tr>
            <td>
                <p style="text-align: center;">${currentDate}</p>
            </td>
            <td>
                <p style="text-align: center;">${customer}</p>
            </td>
            <td>
                <p style="text-align: center;">${version}</p>
            </td>
            <td>
                <p style="text-align: center;"><a href="${androidLink}">Download</a></p>
            </td>
            <td>
                <p style="text-align: center;"><a href="${iosLink}">Download</a></p>
            </td>
        </tr>
    """
}

// Function to update the Confluence page
def updateConfluencePage(pageId, title, updatedBody, currentVersion) {
    def url = "${env.CONFLUENCE_BASE_URL}/pages/${pageId}"
    def payload = [
        version: [number: currentVersion + 1],
        title  : title,
        id: pageId,
        status: "current",
        body   : [
            representation: "storage",
            value: updatedBody
        ]
    ]

    println "Error occurred while fetching Confluence page content: ${updatedBody}"

    def connection = new URL(url).openConnection()
    connection.setRequestProperty("Authorization", "Basic " + "${CONF_USERNAME}:${CONF_PASSWORD}".bytes.encodeBase64().toString())
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestMethod("PUT")
    connection.doOutput = true

    def writer = new OutputStreamWriter(connection.outputStream)
    writer.write(JsonOutput.toJson(payload))
    writer.flush()
    writer.close()
    println "Error occurred while fetching Confluence page content: ${JsonOutput.toJson(payload)}"
    try {
        def responseCode = connection.responseCode
        if (responseCode == 200) {
            def response = connection.inputStream.text
            return new JsonSlurper().parseText(response)
        } else {
            def errorMessage = connection.errorStream?.text ?: "Unknown error"
            println "Failed to fetch Confluence page content. HTTP ${responseCode}: ${errorMessage}"
            return null
        }
    } catch (Exception e) {
        println "Error occurred while fetching Confluence page content: ${e.message}"
        return null
    }
}

return this
