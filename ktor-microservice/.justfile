default:
    @just --summary \
        | tr ' ' '\n' \
        | fzf --prompt="run > " \
              --preview "just \
              --show {}" \
              --layout=reverse \
              --height=20 \
              --style=minimal \
              --preview-window=down:wrap \
        | xargs -r just

build:
  ./gradlew build

format:
  nix fmt

lint:
  nix flake check

[parallel]
develop: start watch

start:
  ./gradlew run

watch:
  ./gradlew -t build

build-image:
    ./gradlew buildImage
    ./gradlew publishImageToLocalRegistry
