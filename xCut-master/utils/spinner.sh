#!/bin/bash

# Author: Tasos Latsas

# spinner.sh
#
# Display an awesome 'spinner' while running your long shell commands
#
# Do *NOT* call _spinner function directly.
# Use {start,stop}_spinner wrapper functions

# usage:
#   1. source this script in your's
#   2. start the spinner:
#       start_spinner [display-message-here]
#   3. run your command
#   4. stop the spinner:
#       stop_spinner [your command's exit status]
#
# Also see: test.sh

# Modified by AbdElraouf Sabri
function _spinner() {
    # $1 start/stop
    #
    # on start: $2 display message
    # on stop : $2 process exit status
    #           $3 spinner function pid (supplied from stop_spinner)

    local on_success="✓"
    local on_fail="✗"

    case $1 in
        start)
            # calculate the column where spinner and status msg will be displayed
            let column=$(tput cols)-${#2}+15
            # display message and position the cursor in $column column
            echo -ne ${2}
            printf "%${column}s"

            # start spinner
            # [ ***  ]
            i=1
            sp=(
                "${BRed}[ ***  ]${Color_Off}"
                "${BRed}[  *** ]${Color_Off}"
                "${BRed}[   ***]${Color_Off}"
                "${BRed}[  *** ]${Color_Off}"
                "${BRed}[ ***  ]${Color_Off}"
                "${BRed}[***   ]${Color_Off}"
                )
            delay=${SPINNER_DELAY:-0.1}

            while :
            do
                printf "\b\b\b\b\b\b\b\b${sp[i++%6]:0}"
                sleep $delay
            done
            ;;
        stop)
            if [[ -z ${3} ]]; then
                echo "spinner is not running.."
                exit 1
            fi

            kill -KILL $3 > /dev/null 2>&1

            # inform the user uppon success or failure
            if [[ $2 -eq 0 ]]; then
                printf "\b\b\b\b\b\b\b\b${BGreen}[ ${on_success} OK ]${Color_Off}\n"
            else
                printf "\b\b\b\b\b\b\b\b${BRed}[ ${on_fail} ER ]${Color_Off}\n"                
            fi
            ;;
        *)
            echo "invalid argument, try {start/stop}"
            exit 1
            ;;
    esac
}

function start_spinner {
    # $1 : msg to display
    _spinner "start" "${1}" &
    # set global spinner pid
    _sp_pid=$!
    disown
}

function stop_spinner {
    # $1 : command exit status
    _spinner "stop" $1 $_sp_pid
    unset _sp_pid
}