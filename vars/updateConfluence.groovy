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

    def body = "<ac:layout><ac:layout-section ac:type=\"two_equal\" ac:breakout-mode=\"default\"><ac:layout-cell><h3>iTravel POS and Dining</h3><table data-table-width=\"760\" data-layout=\"center\" ac:local-id=\"itravel-pos-and-dining\"><colgroup><col style=\"width: 77.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /></colgroup><tbody><tr><th><p style=\"text-align: center;\"><strong>Date</strong></p></th><th><p style=\"text-align: center;\"><strong>Type</strong></p></th><th><p style=\"text-align: center;\"><strong>Version</strong></p></th><th><p style=\"text-align: center;\"><strong>Android</strong></p></th><th><p style=\"text-align: center;\"><strong>iOS</strong></p></th></tr><tr><td><p style=\"text-align: center;\">22/11/2024</p></td><td><p style=\"text-align: center;\">RCYC</p></td><td><p style=\"text-align: center;\">1.0.3(1030)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr><tr><td><p style=\"text-align: center;\">22/11/2024</p></td><td><p style=\"text-align: center;\">RCYC</p></td><td><p style=\"text-align: center;\">1.0.3(1030)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr><tr><td><p style=\"text-align: center;\">22/10/2024</p></td><td><p style=\"text-align: center;\">Internal</p></td><td><p style=\"text-align: center;\">1.0.2(1020)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr></tbody></table></ac:layout-cell><ac:layout-cell><h3>iTravel Crew Navigator</h3><table data-table-width=\"760\" data-layout=\"center\" ac:local-id=\"itravel-crew-navigator\"><colgroup><col style=\"width: 77.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /></colgroup><tbody><tr><th><p style=\"text-align: center;\"><strong>Date</strong></p></th><th><p style=\"text-align: center;\"><strong>Type</strong></p></th><th><p style=\"text-align: center;\"><strong>Version</strong></p></th><th><p style=\"text-align: center;\"><strong>Android</strong></p></th><th><p style=\"text-align: center;\"><strong>iOS</strong></p></th></tr><tr><td><p style=\"text-align: center;\">11/12/2023</p></td><td><p style=\"text-align: center;\">Internal</p></td><td><p style=\"text-align: center;\">1.0.2(1020)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr></tbody></table></ac:layout-cell></ac:layout-section><ac:layout-section ac:type=\"two_equal\" ac:breakout-mode=\"default\"><ac:layout-cell><h3>iTravel Check-in </h3><table data-table-width=\"760\" data-layout=\"center\" ac:local-id=\"itravel-check-in\"><colgroup><col style=\"width: 77.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /></colgroup><tbody><tr><th><p style=\"text-align: center;\"><strong>Date</strong></p></th><th><p style=\"text-align: center;\"><strong>Type</strong></p></th><th><p style=\"text-align: center;\"><strong>Version</strong></p></th><th><p style=\"text-align: center;\"><strong>Android</strong></p></th><th><p style=\"text-align: center;\"><strong>iOS</strong></p></th></tr><tr><td><p style=\"text-align: center;\">10/11/2023</p></td><td><p style=\"text-align: center;\">Internal</p></td><td><p style=\"text-align: center;\">1.0.2(1020)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr><tr><td><p style=\"text-align: center;\">10/12/2024</p></td><td><p style=\"text-align: center;\">RCYC</p></td><td><p style=\"text-align: center;\">1.0.3(1030)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr></tbody></table></ac:layout-cell><ac:layout-cell><h3>iTravel Guest Services</h3><table data-table-width=\"760\" data-layout=\"center\" ac:local-id=\"itravel-guest-services\"><colgroup><col style=\"width: 77.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /><col style=\"width: 71.0px;\" /></colgroup><tbody><tr><th><p style=\"text-align: center;\"><strong>Date</strong></p></th><th><p style=\"text-align: center;\"><strong>Type</strong></p></th><th><p style=\"text-align: center;\"><strong>Version</strong></p></th><th><p style=\"text-align: center;\"><strong>Android</strong></p></th><th><p style=\"text-align: center;\"><strong>iOS</strong></p></th></tr><tr><td><p style=\"text-align: center;\">12/11/2024</p></td><td><p style=\"text-align: center;\">Internal</p></td><td><p style=\"text-align: center;\">1.0.2(1020)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr><tr><td><p style=\"text-align: center;\">12/03/2024</p></td><td><p style=\"text-align: center;\">RCYC</p></td><td><p style=\"text-align: center;\">1.0.3(1030)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr><tr><td><p style=\"text-align: center;\">22/11/2024</p></td><td><p style=\"text-align: center;\">RCYC</p></td><td><p style=\"text-align: center;\">1.0.3(1030)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr><tr><td><p style=\"text-align: center;\">22/11/2024</p></td><td><p style=\"text-align: center;\">RCYC</p></td><td><p style=\"text-align: center;\">1.0.3(1030)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr><tr><td><p style=\"text-align: center;\">22/11/2024</p></td><td><p style=\"text-align: center;\">RCYC</p></td><td><p style=\"text-align: center;\">1.0.3(1030)</p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td><td><p style=\"text-align: center;\"><a href=\"https://www.google.com\">Download</a></p></td></tr></tbody></table></ac:layout-cell></ac:layout-section></ac:layout>"

 
    // if (updatedBody == bodyContent) {
    //     error "No table with ac:local-id '${targetTableId}' was found."
    // }
    def doc = org.jsoup.Jsoup.parse(body)
    def table = doc.select("table").first()
    if (!table) {
        println "Table not found"
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
