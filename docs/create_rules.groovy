import groovy.io.FileType

println '===================================='
println 'Creating markdown rule documentation'
println '===================================='


def ruleSourcePath = '../sonar-pmd-plugin/src/main/resources/org/sonar/l10n/pmd/rules'
def ruleTargetPath = './rules'

def createDeprecationWarning = {
    rule ->
        if (rule) {
            def ruleNumber = rule.substring(1)
            return "> :warning: This rule is **deprecated** in favour of [${rule}](https://rules.sonarsource.com/java/RSPEC-${ruleNumber})."
        }
        ""
}

def extractRuleFromContent = {
    content ->
        def pattern = /(rule):(squid):(\w+)/
        def group = (content =~ /$pattern/)

        if (group.size() > 0) {
            return group[0][3]
        }
}

def createMarkdownPagesForCategory = {
    category ->
        def currentDir = new File("${ruleSourcePath}/${category}")
        currentDir.eachFile FileType.FILES, {
            String rulename = it.name.tokenize('.')[0]
            String htmlContent = it.text
            String deprecationWarning = createDeprecationWarning(extractRuleFromContent(htmlContent))
            String ruleContent = """
# ${rulename}
**Category:** `${category}`<br/>
**Rule Key:** `${category}:${rulename}`<br/>
${deprecationWarning}

-----

${htmlContent}
"""
            def file = new File("${ruleTargetPath}/${rulename}.md").newWriter()
            file << ruleContent
            file.close()
        }
}

createMarkdownPagesForCategory('pmd')
createMarkdownPagesForCategory('pmd-unit-tests')