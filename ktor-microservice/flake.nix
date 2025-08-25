{
  description = "Template for Microservice projects";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    treefmt-nix.url = "github:numtide/treefmt-nix";
  };

  outputs =
    {
      self,
      nixpkgs,
      treefmt-nix,
    }:
    let
      javaVersion = 17;

      supportedSystems = [
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
        "aarch64-darwin"
      ];

      forEachSupportedSystem =
        f:
        nixpkgs.lib.genAttrs supportedSystems (
          system:
          f {
            pkgs = import nixpkgs {
              inherit system;
              overlays = [ self.overlays.default ];
            };
          }
        );

      treefmtEval = forEachSupportedSystem ({ pkgs }: treefmt-nix.lib.evalModule pkgs ./treefmt.nix);
    in
    {
      overlays.default = final: prev: rec {
        jdk = prev."jdk${toString javaVersion}";
        kotlin = prev.kotlin.override { jre = jdk; };
      };

      devShells = forEachSupportedSystem (
        { pkgs }:
        {
          default = pkgs.mkShell {
            packages = with pkgs; [
              jdk
              kotlin
              just
              awscli
            ];
          };
        }
      );

      formatter = forEachSupportedSystem ({ pkgs }: treefmtEval.${pkgs.system}.config.build.wrapper);
      checks = forEachSupportedSystem (
        { pkgs }:
        {
          formatting = treefmtEval.${pkgs.system}.config.build.check self;
        }
      );
    };
}
