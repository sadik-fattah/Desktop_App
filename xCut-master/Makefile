destinationDir=/opt/xCut
THIS_FILE := $(lastword $(MAKEFILE_LIST))

all: install

install: 
	@echo -n "Installing xcut .. " ||:
	@sudo mkdir -p $(destinationDir) ||:
	@sudo cp -r . $(destinationDir) ||:
	@sudo ln -sf $(destinationDir)/xCut /usr/local/bin/xx ||:
	@sudo ln -sf $(destinationDir)/UPDATE /usr/local/bin/xx-update ||:
	@sudo ln -sf $(destinationDir)/UNINSTALL /usr/local/bin/xx-uninstall ||:
	@sudo chmod +x $(destinationDir)/xCut ||:
	@sudo chmod +x $(destinationDir)/UPDATE ||:
	@sudo chmod +x $(destinationDir)/UNINSTALL ||:
	@echo "Done" ||:
	@echo
	@echo "Installation summary" ||:		
	@echo '╔══════════════╦════════════════════════════════════════════════════╗' ||:
	@echo '║   Command    ║                       usage                        ║' ||:
	@echo '╠══════════════╬════════════════════════════════════════════════════╣' ||:
	@echo '║ xx           ║ xCut application                                   ║' ||:
	@echo '║ xx-update    ║ Updates xCut to latest version from the repository ║' ||:
	@echo '║ xx-uninstall ║ Uninstalls xCut completely                         ║' ||:
	@echo '╚══════════════╩════════════════════════════════════════════════════╝' ||:

uninstall:
	@echo -n "Uninstalling xcut .. " ||:
	@sudo rm $(destinationDir)/* -rf ||:
	@sudo rm /usr/local/bin/xx ||:
	@sudo rm /usr/local/bin/xx-update ||:
	@sudo rm /usr/local/bin/xx-uninstall ||:
	@echo "Done" ||:

update:
	@echo "Updating xcut:" ||:
	@echo -n "    " ||:
	@$(MAKE) -f $(THIS_FILE) uninstall --no-print-directory
	@echo -n "    " ||:
	@$(MAKE) -f $(THIS_FILE) install --no-print-directory
	@echo "Done" ||: