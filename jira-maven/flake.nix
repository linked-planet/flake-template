{
  description = throw "Please enter a description";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";
    treefmt-nix.url = "github:numtide/treefmt-nix";
  };

  outputs =
    { self
    , nixpkgs
    , treefmt-nix
    , ...
    }:
    let
      packageName = throw "Please provide a name for this package";
      packageVersion = throw "Pleases provide the version of this package";

      javaVersion = 11;
      nodeVersion = 20;
      postgresVersion = 14;

      supportedSystems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];

      eachSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: let 
          pkgs = nixpkgs.legacyPackages.${system} // { overlays = [ self.overlays.default ]; };
        in f pkgs);

      treefmtEval = eachSystem (pkgs: treefmt-nix.lib.evalModule pkgs ./treefmt.nix);
    in
    rec {
      overlays.default = (final: prev: rec {
        jdk = prev."jdk${toString javaVersion}";
        maven = prev.maven.override { jdk_headless = jdk; };
        kotlin = prev.kotlin.override { jre = jdk; };
        nodejs = prev."nodejs_${toString nodeVersion}";
        postgresql = prev."postgresql_${toString postgresVersion}";
      });

      devShells = eachSystem (pkgs: {
        default = pkgs.mkShell {

          MAVEN_HOME = "${pkgs.maven}/maven";

          packages = with pkgs; [
            gcc
            jdk
            kotlin
            maven
            postgresql
            ncurses
            nodejs
            patchelf
            zlib
          ];
        };
      });

      packages = eachSystem (pkgs: {
        default = (pkgs.maven.buildMavenPackage rec {
          pname = packageName;
          version = packageVersion;

          mvnHash = "sha256-HDfPn6Vdhrgcp9rDJYTiw0AvnlmZ6QCIZrJ1C779Xcg=";

          src = builtins.path { path = ./.; };
          buildInputs = [
            pkgs.maven
            pkgs.jdk
            pkgs.nodejs
          ];

          installPhase = ''
            mkdir -p $out/
            cp "target" $out/share
          '';
        });
      });

      apps = eachSystem (pkgs: {
        pkgs.system.overlays = self.overlays.default;
        deploy = {
          program = pkgs.lib.getExe (pkgs.writeShellScriptBin "upmDeploy" ''
            ${pkgs.maven}/bin/mvn \
              upm:uploadPluginFile \
              -Ddeploy.url=http://localhost:8080 \
              -Ddeploy.username=admin \
              -Ddeploy.password=admin \
              -Dfrontend.devMode=true \
              -Dplugin.file=${packages.${pkgs.system}.default}/share/${packageName}-${packageVersion}.jar \
              -Plocal
          '');
          type = "app";
        };
      });

      formatter = eachSystem (pkgs: treefmtEval.${pkgs.system}.config.build.wrapper);
      # for `nix flake check`
      checks = eachSystem (pkgs: {
        formatting = treefmtEval.${pkgs.system}.config.build.check self;
      });

    defaultTemplate = {
      path = ".";
      description = "Flake for use with atlassian-maven projects";
    };

    # Utilized by `nix flake init -t <flake>#<name>`
    templates.example = self.defaultTemplate;
    };
}
