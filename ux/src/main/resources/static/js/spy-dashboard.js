(function () {
    const uiPath = window.spyConfig?.uiPath || "";
    const refreshIntervalMs = 2000;

    let refreshTimer = null;
    let isRunning = false;

	async function refreshTable() {
	    try {
	        const response = await fetch(uiPath + "/logs/fragment", {
	            headers: { "X-Requested-With": "XMLHttpRequest" }
	        });

	        if (!response.ok) return;

	        const html = await response.text();
	        const container = document.getElementById("logTableContainer");

	        if (container) {
	            container.innerHTML = html;
	            formatJsonBlocks();
	            bindDetailButtons();
				bindCopyButtons();
	        }
	    } catch (e) {
	        console.error("Table refresh failed:", e);
	    }
	}

    async function refreshStats() {
        try {
            const res = await fetch(uiPath + "/api/logs");
            if (!res.ok) return;

            const data = await res.json();

            const total = data.length;
            const avg = total === 0 ? 0 : Math.round(
                data.reduce((sum, x) => sum + (x.duration || 0), 0) / total
            );

            const errors = data.filter(x => x.status >= 400).length;
            const errorRate = total === 0 ? 0 : Math.round((errors / total) * 100);
			const last = total > 0 ? data[0] : null;

            setText("totalRequests", total);
            setText("avgDuration", avg);
            setText("errorRate", errorRate + "%");
            setText("lastRequest", last ? `${last.method || "-"} ${last.uri || "-"}` : "-");
        } catch (e) {
            console.error("Stats refresh failed:", e);
        }
    }
	function bindCopyButtons() {
	    const buttons = document.querySelectorAll(".copy-btn");

	    buttons.forEach(button => {
	        button.addEventListener("click", async function () {
	            const targetId = this.getAttribute("data-copy-target");
	            const target = document.getElementById(targetId);

	            if (!target) return;

	            const text = target.innerText || target.textContent || "";

	            try {
	                await navigator.clipboard.writeText(text);

	                const original = this.innerText;
	                this.innerText = "Copied";
	                setTimeout(() => {
	                    this.innerText = original;
	                }, 1200);
	            } catch (e) {
	                console.error("Copy failed:", e);
	            }
	        });
	    });
	}
	async function clearMemoryStore() {
		const btn = document.getElementById("clearMemoryBtn");
		const lastClearedEl = document.getElementById("lastCleared");

		btn.disabled = true;
		btn.innerHTML = '<i class="bi bi-hourglass"></i>';

		try {
			const response = await fetch(uiPath + "/logs/clear", {
				method: "POST"
			});

			if (!response.ok) {
				alert("Memory store was not cleared");
				return;
			}

			// ✅ backend'den gelen timestamp
			const timestamp = await response.json();

			const date = new Date(timestamp);

			if (lastClearedEl) {
				lastClearedEl.textContent = date.toLocaleTimeString();
			}

			refreshTable();
			refreshStats();

		} catch (err) {
			console.error(err);
			alert("Error");
		} finally {
			btn.disabled = false;
			btn.innerHTML = '<i class="bi bi-trash3"></i>';
		}
	}
	function formatJsonBlocks() {
	    const blocks = document.querySelectorAll(".format-json");

	    blocks.forEach(block => {
	        const raw = (block.textContent || "").trim();

	        if (!raw || raw === "-") {
	            return;
	        }

	        try {
	            const parsed = JSON.parse(raw);
	            block.textContent = JSON.stringify(parsed, null, 2);
	        } catch (e) {
	           
	        }
	    });
	}

    function setText(id, value) {
        const el = document.getElementById(id);
        if (el) {
            el.innerText = value;
        }
    }

    function refreshAll() {
        refreshTable();
        refreshStats();
    }

    function startRefresh() {
        if (refreshTimer) {
            clearInterval(refreshTimer);
        }

        refreshAll();
        refreshTimer = setInterval(refreshAll, refreshIntervalMs);
        isRunning = true;
        updateToggleButton();
    }

    function stopRefresh() {
        if (refreshTimer) {
            clearInterval(refreshTimer);
            refreshTimer = null;
        }

        isRunning = false;
        updateToggleButton();
    }

    function toggleRefresh() {
        if (isRunning) {
            stopRefresh();
        } else {
            startRefresh();
        }
    }

    function updateToggleButton() {
        const btn = document.getElementById("toggleRefreshBtn");
        if (!btn) return;

        if (isRunning) {
            btn.innerText = "⏸ Pause";
            btn.classList.remove("paused");
            btn.classList.add("running");
        } else {
            btn.innerText = "▶ Resume";
            btn.classList.remove("running");
            btn.classList.add("paused");
        }
    }

    function bindEvents() {
        const btn = document.getElementById("toggleRefreshBtn");
        if (btn) {
            btn.addEventListener("click", toggleRefresh);
        }
		
		document.getElementById("clearMemoryBtn")
		    ?.addEventListener("click", clearMemoryStore);
    }
	
	function bindDetailButtons() {
	    const buttons = document.querySelectorAll(".detail-btn");

	    buttons.forEach(button => {
	        button.addEventListener("click", function () {
	            const detailId = this.getAttribute("data-detail-id");
	            const detailRow = document.getElementById(detailId);

	            if (!detailRow) return;

	            detailRow.classList.toggle("hidden");

	            this.innerText = detailRow.classList.contains("hidden") ? "Details" : "Close";
	        });
	    });
	}

    bindEvents();
    startRefresh();
})();