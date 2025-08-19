JAVAC = javac
JAVA = java
SRCDIR = src
BINDIR = bin
SOURCES = $(wildcard $(SRCDIR)/com/gitgui/*.java)
CLASSES = $(SOURCES:$(SRCDIR)/%.java=$(BINDIR)/%.class)

.PHONY: all run clean

all: $(CLASSES)

$(BINDIR)/%.class: $(SRCDIR)/%.java
	@mkdir -p $(BINDIR)/com/gitgui
	$(JAVAC) -d $(BINDIR) $<

run: all
	$(JAVA) -cp $(BINDIR) com.gitgui.GitGUI

clean:
	rm -rf $(BINDIR)

# Create jar file
jar: all
	@echo "Creating JAR file..."
	jar cfe GitGUI.jar com.gitgui.GitGUI -C bin .
	@echo "JAR file created: GitGUI.jar"
	@echo "Run with: java -jar GitGUI.jar"