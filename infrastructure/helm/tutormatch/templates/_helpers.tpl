{{- define "tutormatch.image" -}}
{{- $root := index . 0 -}}
{{- $name := index . 1 -}}
{{- $digest := index . 2 -}}
{{- if $digest -}}
{{- printf "%s/%s@%s" $root.Values.global.imageRegistry $name $digest -}}
{{- else -}}
{{- printf "%s/%s:%s" $root.Values.global.imageRegistry $name (required "global.imageTag must be set when imageDigest is empty" $root.Values.global.imageTag) -}}
{{- end -}}
{{- end -}}

{{- define "tutormatch.keycloakRealmJson" -}}
{{- $realm := .Files.Get "files/tutormatch-realm.json" -}}
{{- if not $realm -}}
{{- fail "Missing chart file files/tutormatch-realm.json. Run: make -C infrastructure sync-keycloak-realm" -}}
{{- end -}}
{{- $realm -}}
{{- end -}}

{{- define "tutormatch.requireDemoSeedFiles" -}}
{{- if not (.Files.Get "files/demo-seed/demo-data.json") -}}
{{- fail "Missing chart file files/demo-seed/demo-data.json. Run: make -C infrastructure sync-demo-seed" -}}
{{- end -}}
{{- if not (.Files.Get "files/demo-seed/seed_demo_data.py") -}}
{{- fail "Missing chart file files/demo-seed/seed_demo_data.py. Run: make -C infrastructure sync-demo-seed" -}}
{{- end -}}
{{- end -}}
